package com.example.sanderbrugge.publictransportassistant.model;

/**
 * Created by sanderbrugge on 23/02/17.
 */

public class GetJSON {
    private Data data;

    public GetJSON(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
