package pl.mbierut.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRateDTO {
    public String no;
    public String effectiveDate;
    public double bid;
    public double ask;


}