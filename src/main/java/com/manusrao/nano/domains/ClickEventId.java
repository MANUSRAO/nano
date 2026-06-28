package com.manusrao.nano.domains;   // adjust to your package

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Composite primary key for {@link ClickEvent}: (id, clicked_at).
 *
 * Field names and types MUST match the @Id fields in the entity exactly:
 *   Long id  <->  ClickEvent.id
 *   Instant clickedAt  <->  ClickEvent.clickedAt
 *
 * @Data gives the all-field equals/hashCode a key class needs; the no-arg
 * constructor is required by JPA for IdClass instantiation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickEventId implements Serializable {

    private Long id;
    private Instant clickedAt;
}
