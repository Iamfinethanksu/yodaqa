package cz.brmlab.yodaqa.pipeline;

import de.tudarmstadt.ukp.dkpro.core.berkeleyparser.BerkeleyParser;
//import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpDependencyParser;
//import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpLemmatizer;
//import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
//import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSemanticRoleLabeler;
//import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
//import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
//import de.tudarmstadt.ukp.dkpro.core.matetools.MateParser;
//import de.tudarmstadt.ukp.dkpro.core.matetools.MatePosTagger;
//import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
//import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
//import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.component.CasDumpWriter;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.resource.ResourceInitializationException;

import cz.brmlab.yodaqa.annotator.WordTokenizer;
import cz.brmlab.yodaqa.annotator.question.SVGenerator;
import cz.brmlab.yodaqa.annotator.question.FocusGenerator;
import cz.brmlab.yodaqa.annotator.question.ClueGenerator;
import cz.brmlab.yodaqa.annotator.question.LATGenerator;
import cz.brmlab.yodaqa.io.debug.DumpConstituents;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createPrimitiveDescription;

/**
 * Annotate the QuestionCAS.
 *
 * This is an aggregate AE that will run a variety of annotators on the
 * QuestionCAS, preparing it for the PrimarySearch and AnswerGenerator
 * stages. */

public class QuestionAnalysis /* XXX: extends AggregateBuilder ? */ {
	public static AnalysisEngineDescription createEngineDescription() throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();

		/* Our way to tokenize (TODO: to be phased out) */
		builder.add(createPrimitiveDescription(WordTokenizer.class));

		/* A bunch of DKpro-bound NLP processors (these are
		 * the giants we stand on the shoulders of) */

		/* XXX: Sorry for the mess below for now. We list all our
		 * alternatives for now, but will clean it up later. */

		/* Token features: */

		builder.add(createPrimitiveDescription(OpenNlpSegmenter.class));

		/* Constituent features and POS features: */

		// slow startup, but important
		builder.add(createPrimitiveDescription(BerkeleyParser.class));

		/* POS features: */

		// Generated by BerkeleyParser
		// fastest:
		//builder.add(createPrimitiveDescription(OpenNlpPosTagger.class));
		/*
		builder.add(createPrimitiveDescription(StanfordPosTagger.class));
		builder.add(createPrimitiveDescription(MatePosTagger.class));
		builder.add(createPrimitiveDescription(ClearNlpPosTagger.class));
		*/

		/* Lemma features: */

		// fastest and handling numbers correctly:
		builder.add(createPrimitiveDescription(StanfordLemmatizer.class));
		/*
		builder.add(createPrimitiveDescription(ClearNlpLemmatizer.class));
		builder.add(createPrimitiveDescription(MateLemmatizer.class));
		*/

		/* Dependency features: */
		// no need for now

		// fastest and correct!
		//builder.add(createPrimitiveDescription(MaltParser.class));
		/*
		// just wrong (Who received the Nobel Prize for Physiology and Medicine in the year 2012?) - everything depends on medicine
		builder.add(createPrimitiveDescription(MateParser.class));
		// a bit wrong (Who received the Nobel Prize for Physiology and Medicine in the year 2012?) - 2012 depends on received
		builder.add(createPrimitiveDescription(ClearNlpDependencyParser.class));
		*/

		/* ...and misc extras: */

		/*
		// too weak, we need very rich NE set
		builder.add(createPrimitiveDescription(StanfordNamedEntityRecognizer.class));
		// also too sparse to be useful
		builder.add(createPrimitiveDescription(ClearNlpSemanticRoleLabeler.class));
		*/


		/* Okay! Now, we can proceed with our key tasks. */

		builder.add(createPrimitiveDescription(SVGenerator.class));
		builder.add(createPrimitiveDescription(FocusGenerator.class));
		builder.add(createPrimitiveDescription(ClueGenerator.class));
		builder.add(createPrimitiveDescription(LATGenerator.class));


		/* Some debug dumps of the intermediate CAS. */
		/* builder.add(createPrimitiveDescription(DumpConstituents.class));
		builder.add(createPrimitiveDescription(
			CasDumpWriter.class,
			CasDumpWriter.PARAM_OUTPUT_FILE, "/tmp/yodaqa-qacas.txt")); */

		return builder.createAggregateDescription();
	}
}
