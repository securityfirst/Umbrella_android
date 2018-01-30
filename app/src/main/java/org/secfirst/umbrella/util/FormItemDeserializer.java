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

public class FormItemDeserializer implements JsonDeserializer<FormItem> {
    @Override
    public FormItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();
        FormItem formItem = new FormItem();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Form.class, new FormDeserializer())
                .registerTypeAdapter(FormScreen.class, new org.secfirst.umbrella.util.FormScreenDeserializer())
                .registerTypeAdapter(FormItem.class, new org.secfirst.umbrella.util.FormItemDeserializer())
                .create();
        String name = jObject.get("name").getAsString();
        if (name==null) {
            throw new JsonParseException("Name not found");
        }
        formItem.setTitle(name);
        String type = jObject.get("type").getAsString();
        if (type==null) {
            throw new JsonParseException("Type not found");
        }
        formItem.setType(type);
        JsonElement label = jObject.get("label");
        formItem.setLabel(label==null ? "" : label.getAsString());

        JsonElement hint = jObject.get("hint");
        formItem.setHint(hint==null ? "" : hint.getAsString());

        ArrayList<FormOption> options = new ArrayList<>();
        JsonArray jOptions = jObject.getAsJsonArray("options");
        if (jOptions!=null) {
            for (JsonElement jOption : jOptions) {
                String optionString = jOption.getAsString();
                if (optionString==null) {
                    throw new JsonParseException("Option not recognized");
                }
                FormOption option = new FormOption(optionString, formItem);
                option.setFormItem(formItem);
                options.add(option);
            }
        }
        formItem.setOptionArrayList(options);
        return formItem;
    }
}