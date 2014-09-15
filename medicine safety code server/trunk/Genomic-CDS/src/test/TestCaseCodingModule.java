package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import exception.BadFormedBase64NumberException;
import exception.BadFormedBinaryNumberException;
import exception.VariantDoesNotMatchAnyAllowedVariantException;

import safetycode.CodingModule;
import safetycode.GenotypeElement;
import utils.OntologyManagement;

/**
 * Test the methods implemented in class CodingModule:
 * - codeListGeneticVariations(ArrayList<GenotypeElement> listGenotype)
 * - decodeListGenotypeVariations(String base64Genotype)
 *
 * @author Jose Antonio Mi�arro Gim�nez
 * @version 2.0
 * @date 15/09/2014
 * */
public class TestCaseCodingModule {
	private OntologyManagement om;
	private String path = "D:/workspace/Genomic-CDS/WebContent";
	
	@Before
	public void initCodingModule(){
		om = OntologyManagement.getOntologyManagement(path);
	}
	
	@Test
	public void testcodeListGeneticVariations() throws BadFormedBinaryNumberException, VariantDoesNotMatchAnyAllowedVariantException{
		String code = CodingModule.codeListGeneticVariations(om.getListGeneticMarkerGroups(), om.getDefaultGenotypeElement());
		assertEquals("We check if the generated code is equal to \"HflRkW-6GrNFmRhFhcd\"","HflRkW-6GrNFmRhFhcd",code);
	}
	
	@Test
	public void testdecodeListGeneticVariations() throws BadFormedBase64NumberException, VariantDoesNotMatchAnyAllowedVariantException{
		String code = "HflRkW-6GrNFmRhFhcd";
		               
		ArrayList<GenotypeElement> listGE = CodingModule.decodeListGenotypeVariations(om.getListGeneticMarkerGroups(), code);
		assertEquals("We check the generated list genotype elements size 28",28,listGE.size());
		assertEquals("We check if the generated variation of the rs113993960 SNP in the genotype is \"CTT;CTT\"","CTT;CTT",listGE.get(0).getCriteriaSyntax());
	}
	
	@Test(expected=BadFormedBase64NumberException.class)
	public void testBadFormedBase64NumberException() throws BadFormedBase64NumberException, VariantDoesNotMatchAnyAllowedVariantException{
		String code = "�";
		CodingModule.decodeListGenotypeVariations(om.getListGeneticMarkerGroups(), code);
	}
}
