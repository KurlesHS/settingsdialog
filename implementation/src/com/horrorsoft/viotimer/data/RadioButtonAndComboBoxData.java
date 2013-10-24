package com.horrorsoft.viotimer.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 24.10.13
 * Time: 0:42
 */
public class RadioButtonAndComboBoxData extends CommonData {


    public int getItemIndexByValue(int value) {
        int returnIndex = -1;
        for (int index = 0; index < getListOfItemData().size(); ++index) {
            if (getListOfItemData().get(index).value == value) {
                returnIndex = index;
                break;
            }
        }
        return returnIndex;
    }

    public enum TypeOfData {
        RadioButton,
        ComboBox
    }

    public class ItemData {
        public ItemData(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int value;
        public String description;
    }

    private int size;
    private TypeOfData typeOfData;
    private List<ItemData> listOfItemData;

    public List<ItemData> getListOfItemData() {
        return listOfItemData;
    }

    public void addItem(int itemValue, String itemDescription) {
        listOfItemData.add(new ItemData(itemValue, itemDescription));
    }

    public RadioButtonAndComboBoxData(TypeOfData typeOfData) {
        this.typeOfData = typeOfData;
        listOfItemData = new ArrayList<ItemData>();
    }

    TypeOfData getTypeOfData() {
        return typeOfData;
    }

    @Override
    public byte[] getBinaryData() {
        return new byte[0];
    }

    @Override
    public boolean isSeparator() {
        return false;
    }

    @Override
    public String getDataDescription() {
        String template = super.getDataDescription();
        template = String.format(template, getDescriptionByValue(getCurrentValue()));
        return template;
    }

    private String getDescriptionByValue(int value) {
        int index = getItemIndexByValue(value);
        if (index < 0) {
            return getTypeOfData() == TypeOfData.ComboBox ?
                    "Undefined description for combobox" :
                    "Undefined description for radiobutton";
        } else {
            return getListOfItemData().get(index).description;
        }
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
