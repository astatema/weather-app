package com.astatema.weatherapp.datamodels;

import java.util.List;

public class Data {
    private List<Request> request;
    private List<CurrentCondition> current_condition;

    public Request getRequest() {
        return request.get(0);
    }

    public CurrentCondition getCurrent_condition() {
        return current_condition.get(0);
    }
}
