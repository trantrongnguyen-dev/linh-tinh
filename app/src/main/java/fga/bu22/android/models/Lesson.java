package fga.bu22.android.models;

import java.io.Serializable;

/**
 * Created by CTC_TRAINING on 4/17/2018.
 */

public class Lesson implements Serializable {

    private String name;

    public Lesson(String name) {
        this.name = name;
    }

    public Lesson() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
