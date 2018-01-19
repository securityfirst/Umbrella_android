package org.secfirst.umbrella.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.secfirst.umbrella.models.FormItem;
import org.secfirst.umbrella.models.FormScreen;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FormScreenDeserializer implements JsonDeserializer<FormScreen> {
    @Override
    public FormScreen deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();
        FormScreen formScreen = new FormScreen();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(FormScreen.class, new org.secfirst.umbrella.util.FormScreenDeserializer())
                .registerTypeAdapter(FormItem.class, new org.secfirst.umbrella.util.FormItemDeserializer())
                .create();
        String name = jObject.get("name").getAsString();
        if (name==null) {
            throw new JsonParseException("Name not found");
        }
        formScreen.setTitle(name);
        ArrayList<FormItem> items = new ArrayList<>();
        JsonArray jItems = jObject.getAsJsonArray("items");
        for (JsonElement jItem : jItems) {
            FormItem item = gson.fromJson(jItem.getAsJsonObject(), FormItem.class);
            item.setFormScreen(formScreen);
            items.add(item);
        }
        formScreen.setItemArrayList(items);
        return formScreen;
    }
}