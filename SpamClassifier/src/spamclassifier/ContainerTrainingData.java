/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spamclassifier;

import java.util.Enumeration;
import java.util.Hashtable;


/// This class is designed with the intention to conveniently transfer multiple variables between different classes.
/// It contains all the data to be carried from training to the classification in a compact way.
public class ContainerTrainingData {
    
    // A hash table for the vocabulary (word searching is very fast in a hash table)
    private Hashtable <String, Bayespam.Multiple_Counter> vocab = new Hashtable <String, Bayespam.Multiple_Counter> ();
    
    private int nMessagesRegular;
    private int nMessagesSpam;
    private int nMessagesTotal;
    private double p_regular;
    private double p_spam;
    private int nWordsRegular;
    private int nWordsSpam;

    public Hashtable<String, Bayespam.Multiple_Counter> getVocab() {
        return vocab;
    }

    public void setVocab(Hashtable<String, Bayespam.Multiple_Counter> vocab) {
        this.vocab = vocab;
    }

    public int getnMessagesRegular() {
        return nMessagesRegular;
    }

    public void setnMessagesRegular(int nMessagesRegular) {
        this.nMessagesRegular = nMessagesRegular;
    }

    public int getnMessagesSpam() {
        return nMessagesSpam;
    }

    public void setnMessagesSpam(int nMessagesSpam) {
        this.nMessagesSpam = nMessagesSpam;
    }

    public int getnMessagesTotal() {
        return nMessagesTotal;
    }

    public void setnMessagesTotal(int nMessagesTotal) {
        this.nMessagesTotal = nMessagesTotal;
    }

    public double getP_regular() {
        return p_regular;
    }

    public void setP_regular(double p_regular) {
        this.p_regular = p_regular;
    }

    public double getP_spam() {
        return p_spam;
    }

    public void setP_spam(double p_spam) {
        this.p_spam = p_spam;
    }

    public int getnWordsRegular() {
        return nWordsRegular;
    }

    public void setnWordsRegular(int nWordsRegular) {
        this.nWordsRegular = nWordsRegular;
    }

    public int getnWordsSpam() {
        return nWordsSpam;
    }

    public void setnWordsSpam(int nWordsSpam) {
        this.nWordsSpam = nWordsSpam;
    }
    
    
    
    // Print the current content of the vocabulary
    public void printVocab()
    {
        Bayespam.Multiple_Counter counter = new Bayespam.Multiple_Counter();

        for (Enumeration<String> e = vocab.keys() ; e.hasMoreElements() ;)
        {   
            String word;
            
            word = e.nextElement();
            counter  = vocab.get(word);
            
            System.out.println( word + " | in regular: " + counter.getRegularCount() + 
                                " in spam: "    + counter.getSpamCount());
        }
    }
    
}
