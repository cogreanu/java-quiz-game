/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long questionId;

    public String id;
    @JsonProperty(value = "image_path")
    public String imagePath;
    public String title;
    @JsonProperty(value = "consumption_in_wh")
    public Long consumptionInWh;
    @Column(columnDefinition = "varchar(510)")
    public String source;

    public Activity() {
    }

    /**
     * Constructor for the Activity
     *
     * @param id              the String containing the id
     * @param imagePath       the String containing the imagePath
     * @param title           the String containing the title
     * @param consumptionInWh the int containing the consumptionInWh
     * @param source          the String containing the source
     */
    public Activity(String id, String imagePath, String title, Long consumptionInWh, String source) {
        this.id = id;
        this.imagePath = imagePath;
        this.title = title;
        this.consumptionInWh = consumptionInWh;
        this.source = source;
    }

    /**
     * Getter for the questionId
     *
     * @return the questionId
     */
    public Long getQuestionId() {
        return questionId;
    }

    /**
     * Setter for the questionId
     *
     * @param questionId the new questionId
     */
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    /**
     * Compares this Activity to another object
     *
     * @param o the object to compare with
     * @return true iff o equals this Activity
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return consumptionInWh.equals(activity.consumptionInWh) && Objects.equals(id, activity.id)
                && Objects.equals(imagePath, activity.imagePath) && Objects.equals(title, activity.title)
                && Objects.equals(source, activity.source);
    }

    /**
     * Hashes the Activity
     *
     * @return the hashCode created
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, imagePath, title, consumptionInWh, source);

    }

    @Override
    public String toString() {
        return "Activity{" +
                "questionId=" + questionId +
                ", id='" + id + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", title='" + title + '\'' +
                ", consumptionInWh=" + consumptionInWh +
                ", source='" + source + '\'' +
                '}';
    }
}
