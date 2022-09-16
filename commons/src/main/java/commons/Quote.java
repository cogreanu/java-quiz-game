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

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    public Person person;
    public String quote;

    @SuppressWarnings("unused")
    private Quote() {
    }

    /**
     * Constructor for Quote
     *
     * @param person the Person to be used
     * @param quote  the String containing the quote
     */
    public Quote(Person person, String quote) {
        this.person = person;
        this.quote = quote;
    }

    /**
     * Compares this Quote to another object
     *
     * @param o the object to compare with
     * @return true iff o equals this Quote
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote1 = (Quote) o;
        return id == quote1.id && Objects.equals(person, quote1.person) && Objects.equals(quote, quote1.quote);
    }

    /**
     * Hashes the Quote
     *
     * @return the hashCode created
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, person, quote);
    }

    /**
     * Returns a String that describes the Quote in human-readable format
     *
     * @return a String that contains the attributes of the Quote
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    //I changed these methods because intellij said that the equals method did not check something
//    @Override
//    public boolean equals(Object obj) {
//        return EqualsBuilder.reflectionEquals(this, obj);
//    }
//
//    @Override
//    public int hashCode() {
//        return HashCodeBuilder.reflectionHashCode(this);
//    }
}