/**
 * 
 */
package org.sunbird.search;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.sunbird.common.JsonUtils;
import org.sunbird.common.Platform;
import org.sunbird.search.client.ElasticSearchUtil;
import org.sunbird.search.processor.SearchProcessor;
import org.sunbird.search.util.SearchConstants;

import java.util.Map;

/**
 * @author pradyumna
 *
 */
public class BaseSearchTest {

	protected static SearchProcessor searchprocessor = new SearchProcessor();

	@BeforeClass
	public static void beforeTest() throws Exception {
		createCompositeSearchIndex();
		Thread.sleep(3000);
	}

	@AfterClass
	public static void afterTest() throws Exception {
		System.out.println("deleting index: " + SearchConstants.COMPOSITE_SEARCH_INDEX);
		ElasticSearchUtil.deleteIndex(SearchConstants.COMPOSITE_SEARCH_INDEX);
	}

	protected static void createCompositeSearchIndex() throws Exception {
		SearchConstants.COMPOSITE_SEARCH_INDEX = "testbadge";
		ElasticSearchUtil.initialiseESClient(SearchConstants.COMPOSITE_SEARCH_INDEX,
				Platform.config.getString("search.es_conn_info"));
		System.out.println("creating index: " + SearchConstants.COMPOSITE_SEARCH_INDEX);
		String settings = "{\"mapping\":{\"total_fields\":{\"limit\":\"1050\"}},\"analysis\":{\"analyzer\":{\"cs_index_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"mynGram\"]},\"cs_search_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"standard\",\"lowercase\"]},\"keylower\":{\"tokenizer\":\"keyword\",\"filter\":\"lowercase\"}},\"filter\":{\"mynGram\":{\"type\":\"nGram\",\"min_gram\":1,\"max_gram\":20,\"token_chars\":[\"letter\",\"digit\",\"whitespace\",\"punctuation\",\"symbol\"]}}}}";
		String mappings = "{\"dynamic_templates\":[{\"nested\":{\"match_mapping_type\":\"object\",\"mapping\":{\"type\":\"nested\",\"fields\":{\"type\":\"nested\"}}}},{\"longs\":{\"match_mapping_type\":\"long\",\"mapping\":{\"type\":\"long\",\"fields\":{\"raw\":{\"type\":\"long\"}}}}},{\"booleans\":{\"match_mapping_type\":\"boolean\",\"mapping\":{\"type\":\"boolean\",\"fields\":{\"raw\":{\"type\":\"boolean\"}}}}},{\"doubles\":{\"match_mapping_type\":\"double\",\"mapping\":{\"type\":\"double\",\"fields\":{\"raw\":{\"type\":\"double\"}}}}},{\"dates\":{\"match_mapping_type\":\"date\",\"mapping\":{\"type\":\"date\",\"fields\":{\"raw\":{\"type\":\"date\"}}}}},{\"strings\":{\"match_mapping_type\":\"string\",\"mapping\":{\"type\":\"text\",\"copy_to\":\"all_fields\",\"analyzer\":\"cs_index_analyzer\",\"search_analyzer\":\"cs_search_analyzer\",\"fields\":{\"raw\":{\"type\":\"text\",\"analyzer\":\"keylower\",\"fielddata\":true}}}}}],\"properties\":{\"all_fields\":{\"type\":\"text\",\"analyzer\":\"cs_index_analyzer\",\"search_analyzer\":\"cs_search_analyzer\",\"fields\":{\"raw\":{\"type\":\"text\",\"analyzer\":\"keylower\"}}}}}";
		ElasticSearchUtil.addIndex(SearchConstants.COMPOSITE_SEARCH_INDEX,
				SearchConstants.COMPOSITE_SEARCH_INDEX_TYPE, settings, mappings);
	}

	protected static void addToIndex(String uniqueId, Map<String, Object> doc) throws Exception {
		String jsonIndexDocument = JsonUtils.serialize(doc);
		ElasticSearchUtil.addDocumentWithId(SearchConstants.COMPOSITE_SEARCH_INDEX,
				SearchConstants.COMPOSITE_SEARCH_INDEX_TYPE, uniqueId, jsonIndexDocument);
	}

}
