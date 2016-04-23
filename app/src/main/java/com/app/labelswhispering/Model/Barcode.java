package com.app.labelswhispering.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Barcode")
public class Barcode extends ParseObject {
    public Barcode() {

    }

    public int getBarcode() {
        return getInt("barcodeId");
    }

    public void setBarcode(int barcodeId) {
        put("barcodeId", barcodeId);
    }

    public String getMedicineId() {
        return getString("medicineId");
    }

    public void setMedicineId(String medicineId) {
        put("medicineId", medicineId);
    }

    public String getManufacturer() {
        return getString("Manufacturer");
    }

    public void setManufacturer(String manufacturer) {
        put("Manufacturer", manufacturer);
    }


}
