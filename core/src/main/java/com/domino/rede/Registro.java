package com.domino.rede;

import com.domino.logica.Tipo;
import com.esotericsoftware.kryo.Kryo;

public class Registro {
    // registro das classes que serão transferidas pela rede, para saber como traduzir os bytes de volta para objetos
    public static void registrarClasses(Kryo kryo){
        // informações sobre o enum de tipo
        kryo.register(Tipo.class);
        
    }

}
