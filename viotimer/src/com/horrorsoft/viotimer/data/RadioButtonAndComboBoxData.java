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

    public int getCurrentIndex() {
        return getItemIndexByValue(getCurrentValue());
    }

    public void setCurrentValueByIndex(int selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < listOfItemData.size()) {
            setCurrentValue(listOfItemData.get(selectedIndex).value);
        }
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
    private int typeOfData;
    private List<ItemData> listOfItemData;

    public List<ItemData> getListOfItemData() {
        return listOfItemData;
    }

    public void addItem(int itemValue, String itemDescription) {
        listOfItemData.add(new ItemData(itemValue, itemDescription));
    }

    public RadioButtonAndComboBoxData(int typeOfData) {
        this.typeOfData = typeOfData;
        listOfItemData = new ArrayList<ItemData>();
    }

    @Override
    public int getType() {
        return typeOfData;
    }

    @Override
    public boolean isEditable() {
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
            return getType() == ICommonData.TYPE_COMBOBOX ?
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
