import java.util.ArrayList;
public class TxHandler {


    UTXOPool pool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        pool = new UTXOPool(utxoPool); 
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {

        ArrayList<UTXO> in_outs = new ArrayList<UTXO>(); //sublime highlighting bug?
        double inputTotal = 0;
        double outputTotal = 0;
        int n = tx.numInputs();
        for(int i = 0; i < n; i++){
            Transaction.Input in = tx.getInput(i);
            UTXO u = new UTXO(in.prevTxHash, in.outputIndex);
            Transaction.Output o = pool.getTxOutput(u);

            //(1) 
            if(o == null){
            	return false;
            }
            //(2) 
            if(!Crypto.verifySignature(o.address,
                tx.getRawDataToSign(i), 
                in.signature)){
                return false; 
            }
            //(3) Can be more efficient? 
            if(in_outs.contains(u)){
            	return false;
            } else {
            	in_outs.add(u);
            }
            //(5)
            inputTotal += o.value;
        }
        n = tx.numOutputs();
        for(int i = 0; i < n; i++){
            //(4) 
            Transaction.Output o = tx.getOutput(i);
            if(o.value < 0){
            	return false;
            }
            //(5)
            outputTotal += o.value;
        }
        if(inputTotal < outputTotal){
        	return false;
        }
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        ArrayList<Transaction> v_trans = new ArrayList<Transaction>();
        for(int i = 0; i < possibleTxs.length; i++){
            Transaction t = possibleTxs[i];
            if(isValidTx(t)){
                v_trans.add(t);
                //Remove all the unspent transactions that are now going to be spent
                for(int j = 0; j < t.numInputs(); j++){
                    Transaction.Input in = t.getInput(j);
                    UTXO u = new UTXO(in.prevTxHash, in.outputIndex);
                    pool.removeUTXO(u);
                }
                for(int k = 0; k < t.numOutputs(); k++){
                	Transaction.Output o = t.getOutput(k);
                	UTXO u = new UTXO(t.getHash() , k);
                	pool.addUTXO(u, o);
                }
            }
        }
        return v_trans.toArray(new Transaction[v_trans.size()]);
    }

}
