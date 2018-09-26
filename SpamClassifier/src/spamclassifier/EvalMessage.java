package spamclassifier;

import java.util.Hashtable;
import spamclassifier.Bayespam.MessageType;

/// This container contains the type of the e-mail it represents from the evaluation (test) set and the list of
/// words contained in that e-mail
public class EvalMessage {
    /// A hash table per evaluation input message containing the words containd in eval-e-mail plus the respective word count
    private Hashtable <String, Integer> vocabEval = new Hashtable <String, Integer> ();
    
    private MessageType type;
    
    public EvalMessage(Hashtable <String, Integer> vocab, MessageType type){
        vocabEval = vocab;
        this.type = type;
    }

    public Hashtable<String, Integer> getVocabEval() {
        return vocabEval;
    }

    public MessageType getType() {
        return type;
    }
    
    
}
