package SyntacticParser;

import ContextFreeGrammar.*;
import Corpus.Sentence;
import ParseTree.*;

import java.util.ArrayList;

public class CYKParser implements SyntacticParser{

    public ArrayList<ParseTree> parse(ContextFreeGrammar cfg, Sentence sentence) {
        int i, j, k, x, y;
        long start, end;
        PartialParseList table[][];
        ParseNode leftNode, rightNode;
        ArrayList<Rule> candidates;
        ArrayList<ParseTree> parseTrees = new ArrayList<ParseTree>();
        table = new PartialParseList[sentence.wordCount()][sentence.wordCount()];
        for (i = 0; i < sentence.wordCount(); i++)
            for (j = i; j < sentence.wordCount(); j++)
                table[i][j] = new PartialParseList();
        for (i = 0; i < sentence.wordCount(); i++){
            candidates = cfg.getTerminalRulesWithRightSideX(new Symbol(sentence.getWord(i).getName()));
            for (Rule candidate: candidates){
                table[i][i].addPartialParse(new ParseNode(new ParseNode(new Symbol(sentence.getWord(i).getName())), candidate.getLeftHandSide()));
            }
        }
        for (j = 1; j < sentence.wordCount(); j++){
            start = System.currentTimeMillis();
            for (i = j - 1; i >= 0; i--)
                for (k = i; k < j; k++){
                    for (x = 0; x < table[i][k].size(); x++)
                        for (y = 0; y < table[k + 1][j].size(); y++){
                            leftNode = table[i][k].getPartialParse(x);
                            rightNode = table[k + 1][j].getPartialParse(y);
                            candidates = cfg.getRulesWithTwoNonTerminalsOnRightSide(leftNode.getData(), rightNode.getData());
                            for (Rule candidate: candidates){
                                table[i][j].addPartialParse(new ParseNode(leftNode, rightNode, candidate.getLeftHandSide()));
                            }
                        }
                }
            end = System.currentTimeMillis();
            System.out.println("Word " + j + " completed in " + (end - start) + " milliseconds");
        }
        for (i = 0; i < table[0][sentence.wordCount() - 1].size(); i++){
            if (table[0][sentence.wordCount() - 1].getPartialParse(i).getData().getName().equals("S")) {
                ParseTree parseTree = new ParseTree(table[0][sentence.wordCount() - 1].getPartialParse(i));
                parseTree.correctParents();
                parseTree.removeXNodes();
                parseTrees.add(parseTree);
            }
        }
        return parseTrees;
    }
}