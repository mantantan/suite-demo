package yonyou.esn.openapi.uitils;

import com.alibaba.fastjson.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {

    public static Logger logger =  LoggerFactory.getLogger(MapUtil.class);

    /***
     * 将json数据转换成map
     * @param jsonText
     * @return
     */
    public static Map<String,Object> jsonToMap(String jsonText){
        if(!StringUtils.isEmpty(jsonText)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(jsonText,Map.class);
            } catch (IOException e) {
                logger.error("parse json string error:{}==={}",jsonText, e);
            }
        }
        return null;
    }
    /***
     * 将map数据转换成json
     * @param params
     * @return
     */
    public static String mapToJson(Map<String,Object> params){
        String json = null;
        if(null != params){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                json = objectMapper.writeValueAsString(params);
            } catch (JsonProcessingException e) {
                logger.error("parse map to json error:{}==={}",params, e);
            }
        }
        return json;

    }

    /***
     * 将xml转成Map,目前支持一层XML解析
     * @param xmlText
     * @return
     * @throws Exception
     */
    public static Map<String,String> xmlToMap(String xmlText){
        Document document = null;
        SAXReader reader = new SAXReader();
        InputStream in = null;
        InputStreamReader strInStream = null;
        try{
            in = new ByteArrayInputStream(xmlText.getBytes("UTF-8"));
            strInStream = new InputStreamReader(in, "UTF-8");
            document = reader.read(strInStream);
            List<Element> elements = document.getRootElement().elements();
            Map<String,String> map = new HashMap<String,String>();
            for(Element element:elements){
                map.put(element.getName(),element.getText());
            }
            return map;
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }finally {
            IOUtils.close(in);
            IOUtils.close(strInStream);
        }
       return null;
    }

}
