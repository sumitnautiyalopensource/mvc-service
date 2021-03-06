package managers;

import org.json.simple.JSONObject;
import org.sunbird.common.JsonUtils;
import org.sunbird.search.util.SearchConstants;

import java.util.LinkedHashMap;

public class GetContentDefinition {
    EventObjectProducer eventObjectProducer = new EventObjectProducer();
    EventProducer eventProducer = new EventProducer();
    public void getDefinition(JSONObject contentobj,String sourceurl) {
        String resp = "", respcode = "", contentId = "";
        JSONObject failedObj = new JSONObject();
        try {
            respcode = Postman.GET(sourceurl).get("statuscode").toString();
            // check if source url is valid or not
            if (respcode.equals("200")) {
                contentId = sourceurl.substring(sourceurl.lastIndexOf('/') + 1);
                resp = Postman.GET(SearchConstants.dikshaurl + SearchConstants.contentreadapi + contentId).get("response").toString();
                JSONObject respobj = JsonUtils.deserialize(resp,JSONObject.class);
                LinkedHashMap<String,Object> result = (LinkedHashMap<String,Object>) respobj.get("result");

                LinkedHashMap<String,Object> content = (LinkedHashMap<String,Object>) result.get("content");
                JSONObject newobj = new JSONObject(content);
                // club metadata of diksha and from csv/json
                newobj.putAll(contentobj);
                eventObjectProducer.addToEventObj(newobj, contentId);
            } else {
                // if source url is not valid , send an event to another topic
                failedObj.put("sourceURL", sourceurl);
                eventProducer.writeToKafka(failedObj.toString(), SearchConstants.mvcFailedtopic);
            }
        }
        catch(Exception e)
        {
        System.out.println(e);
            failedObj.put("sourceURL", sourceurl);
            eventProducer.writeToKafka(failedObj.toString(), SearchConstants.mvcFailedtopic);
        }
     }
   }
