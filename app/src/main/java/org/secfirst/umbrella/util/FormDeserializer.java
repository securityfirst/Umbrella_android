package org.secfirst.umbrella.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.secfirst.umbrella.models.Form;
import org.secfirst.umbrella.models.FormItem;
import org.secfirst.umbrella.models.FormOption;
import org.secfirst.umbrella.models.FormScreen;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FormDeserializer implements JsonDeserializer<Form> {
    @Override
    public Form deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();
        Form form = new Form();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Form.class, new FormDeserializer())
                .registerTypeAdapter(FormScreen.class, new FormScreenDeserializer())
                .registerTypeAdapter(FormItem.class, new org.secfirst.umbrella.util.FormItemDeserializer())
                .registerTypeAdapter(FormOption.class, new FormItemDeserializer())
                .create();
        String name = jObject.get("name").getAsString();
        if (name==null) {
            throw new JsonParseException("Name not found");
        }
        form.setTitle(name);
        form.setId(jObject.get("id").getAsString());
        ArrayList<FormScreen> screens = new ArrayList<>();
        JsonArray jScreens = jObject.getAsJsonArray("screens");
        for (JsonElement jScreen : jScreens) {
            FormScreen screen = gson.fromJson(jScreen.getAsJsonObject(), FormScreen.class);
            screen.setForm(form);
            screens.add(screen);
        }
        form.setScreenArrayList(screens);
        return form;
    }
}