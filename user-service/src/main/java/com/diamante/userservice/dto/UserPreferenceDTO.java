package com.diamante.userservice.dto;

import com.diamante.userservice.model.FaixaPreco;
import com.diamante.userservice.model.TipoComida;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferenceDTO {

    private List<TipoComida> tiposComidaPreferidos;
    private FaixaPreco faixaPrecoPreferida;
    private String localizacao;
}
