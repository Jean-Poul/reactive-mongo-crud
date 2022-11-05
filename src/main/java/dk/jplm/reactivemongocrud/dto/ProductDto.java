package dk.jplm.reactivemongocrud.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductDto {

    private String id;
    private String name;
    private int qty;
    private double price;

}
