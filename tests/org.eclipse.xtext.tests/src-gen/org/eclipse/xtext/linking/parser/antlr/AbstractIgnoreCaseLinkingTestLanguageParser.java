/*
 * generated by Xtext
 */
package org.eclipse.xtext.linking.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.linking.services.AbstractIgnoreCaseLinkingTestLanguageGrammarAccess;

public class AbstractIgnoreCaseLinkingTestLanguageParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private AbstractIgnoreCaseLinkingTestLanguageGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	
	@Override
	protected org.eclipse.xtext.linking.parser.antlr.internal.InternalAbstractIgnoreCaseLinkingTestLanguageParser createParser(XtextTokenStream stream) {
		return new org.eclipse.xtext.linking.parser.antlr.internal.InternalAbstractIgnoreCaseLinkingTestLanguageParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "Model";
	}
	
	public AbstractIgnoreCaseLinkingTestLanguageGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(AbstractIgnoreCaseLinkingTestLanguageGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}
