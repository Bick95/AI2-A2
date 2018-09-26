/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spamclassifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author daniel
 */
public class Classifier {
    private ContainerTrainingData container = new ContainerTrainingData();                      /// Training data
    private ArrayList<EvalMessage> evaluationMessages = new ArrayList<EvalMessage>();   /// Evaluation data
    
    
    public Classifier(ContainerTrainingData container, String path) throws IOException{
        ///Get evaluation data:
        MessagesReader reader = new MessagesReader();
        this.evaluationMessages = reader.getEvaluationSet(path);
        
        this.container = container;
    }
    
    private Bayespam.MessageType classifyMessage(EvalMessage message){
        
        Hashtable<String, Integer> vocabEval = message.getVocabEval();
        
        
        /// Calculate probability that message is a regular one or spam one respectively
        double conditional_p_regular = 0.0;        
        conditional_p_regular = container.getP_regular();   /// set class a priori
        
        double conditional_p_spam = 0.0;        
        conditional_p_spam = container.getP_spam();         /// set class a priori
        
        
        for (String keyEval : vocabEval.keySet()){
            //System.out.println("Key: " + keyEval + " Value: " + vocabEval.get(keyEval));
            if (container.getVocab().keySet().contains(keyEval)){
                conditional_p_regular = conditional_p_regular + (double) vocabEval.get(keyEval) * (double) container.getVocab().get(keyEval).getLikelihoodRegular();
                conditional_p_spam = conditional_p_spam + (double) vocabEval.get(keyEval) * (double) container.getVocab().get(keyEval).getLikelihoodSpam();
                //System.out.println("Key: " + keyEval + " present: " + (double) vocabEval.get(keyEval) + " times. Value: " + (double) container.getVocab().get(keyEval).getLikelihoodRegular());
            }
        }
        System.out.println("conditional_p_regular: " + conditional_p_regular + " conditional_p_spam: " + conditional_p_spam);
        /// Return NORMAL if log-posteri class probability for the message being regular/normal is higher than that of the message being spam, otherwise return SPAM
        return (conditional_p_regular > conditional_p_spam ? Bayespam.MessageType.NORMAL : Bayespam.MessageType.SPAM);
    }
    
    
    private ArrayList<Bayespam.MessageType> classifyAll(){
        ArrayList<Bayespam.MessageType> classifications = new ArrayList<Bayespam.MessageType>();
        for (EvalMessage message : evaluationMessages){
            classifications.add(classifyMessage(message));
        }
        return classifications;
    }
    
    public BigDecimal roundDouble(double x, int decimalPlaces) 
{
    BigDecimal rounded = new BigDecimal(Double.toString(x));
    rounded = rounded.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);       
    return rounded;
}
    
    private void printConfusionMatrix(int normalCorrect, int spamCorrect, int t1Error, int t2Error){
        double pSpamCorrect = (double) spamCorrect / ((double) spamCorrect + (double) t2Error) * 100.0;
        double pNormalCorrect = (double) normalCorrect / ((double) t1Error + (double) normalCorrect) * 100.0;
        System.out.println("Confusion Matrix:");
        System.out.println("Real class \\ Classified as:\tspam\tregular\t:total");
        System.out.println("spam:\t\t\t\t" + spamCorrect + "\t" + t2Error + "\t: " + (spamCorrect + t2Error) + " (" + roundDouble(pSpamCorrect, 2) + " % correct)");
        System.out.println("regular:\t\t\t" + t1Error + "\t" + normalCorrect + "\t: " + (t1Error + normalCorrect) + " (" + roundDouble(pNormalCorrect, 2) + " % correct)");
        
    }
    
    public void eval(){
        // Print out the hash table contained in container
        container.printVocab();
        
        int t1Error = 0; /// Type 1-Error: Normal message classified as spam
        int t2Error = 0; /// Type 2-Error: Spam message classified as normal message
        int normalCorrect = 0;
        int spamCorrect = 0;
        ArrayList<Bayespam.MessageType> classifications = classifyAll();
        
        for (int i = 0; i < classifications.size(); i++){
            System.out.println("True: " + evaluationMessages.get(i).getType() + " Prodeicted: " + classifications.get(i));
            if (evaluationMessages.get(i).getType() == classifications.get(i)){ /// Correct classification
                if (evaluationMessages.get(i).getType() == Bayespam.MessageType.NORMAL)
                    normalCorrect++;
                else
                    spamCorrect++;
            } else { /// No correct classification
                if (evaluationMessages.get(i).getType() == Bayespam.MessageType.NORMAL) /// Message is of type NORMAL and was classified as spam - false positive
                    t1Error++;
                else                                                                    /// Message is of type SPAM and was classified as Normal - false negative
                    t2Error++;
            }
        }
        
        printConfusionMatrix(normalCorrect, spamCorrect, t1Error, t2Error);
    }
}