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
        int inputTotal = 0;
        int outputTotal = 0;
        for(int i = 0; i < tx.numInputs(); i++){
            Transaction.Input in = tx.getInput(i);
            UTXO u = new UTXO(prevTxHash, outputIndex);
            Transaction.Output o = pool.getTxOutput(u);

            //(1) 
            if(o == null){return false;}
            //(2) 
            if(!Crypto.verifySignature(o.address,
                tx.getRawDataToSign(i), 
                in.signature)){
                return false; 
            }
            //(3) Can be more efficient? 
            if(in_outs.contains(u)){return false;} 
            else {in_outs.add(u);}
            //(5)
            inputTotal += o.value;

        }
        for(int i = 0; i < tx.numOutputs(); i++){
            //(4) 
            Transaction.Output o = tx.getOutput(i);
            if(o.value < 0){return false;}
            //(5)
            outputTotal += 0.value;
        }
        if(inputTotal < outputTotal){return false;}
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        return null;
    }

}
