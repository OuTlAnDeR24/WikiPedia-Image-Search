package com.example.aashish.imagesearch;

import com.example.aashish.imagesearch.models.ApiResult;
import com.example.aashish.imagesearch.models.Page;
import com.example.aashish.imagesearch.models.Thumbnail;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aashish on 6/6/16.
 * Gson Deserialiser class which parses the received JSON response
 */
public class Deserializer {

    public static class PageDeserializer implements JsonDeserializer<ApiResult> {
        @Override
        public ApiResult deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            ApiResult apiResultToReturn = new ApiResult();
            if (json.isJsonNull()) {
                return null;
            } else {
                JsonObject jsonObject = json.getAsJsonObject();
                if (jsonObject.has("query")) {
                    JsonObject queryObject = jsonObject.getAsJsonObject("query");
                    if (queryObject.has("pages")) {
                        JsonObject pagesObject = queryObject.getAsJsonObject("pages");
                        try {
                            JSONObject obj = new JSONObject(pagesObject.toString());
                            List<Page> pagesToReturn = new ArrayList<>();
                            for (int i = 0; i < obj.length(); i++) {
                                JSONObject pageObject = obj.getJSONObject(obj.names().get(i).toString());
                                Page page = new Page();
                                page.setPageId(pageObject.optInt("pageid", 0));
                                page.setTitle(pageObject.optString("title"));
                                if (pageObject.has("thumbnail")) {
                                    JSONObject thumbnailObject = pageObject.getJSONObject("thumbnail");
                                    Thumbnail thumbnail = new Thumbnail();
                                    thumbnail.setUrl(thumbnailObject.getString("source"));
                                    thumbnail.setWidth(thumbnailObject.getInt("width"));
                                    thumbnail.setHeight(thumbnailObject.getInt("height"));
                                    page.setThumbnail(thumbnail);
                                }
                                pagesToReturn.add(page);
                            }
                            apiResultToReturn.setPages(pagesToReturn);
                            return apiResultToReturn;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }
    }

}
