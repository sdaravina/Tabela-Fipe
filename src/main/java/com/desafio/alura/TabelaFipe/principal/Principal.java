package com.desafio.alura.TabelaFipe.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.desafio.alura.TabelaFipe.model.Dados;
import com.desafio.alura.TabelaFipe.model.Modelos;
import com.desafio.alura.TabelaFipe.model.Veiculo;
import com.desafio.alura.TabelaFipe.service.ConsumoApi;
import com.desafio.alura.TabelaFipe.service.ConverteDados;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() throws InterruptedException{
        var menu = """
                *** OPÇÕES DISPONÍVEIS ***

                Carro
                Moto
                Caminhão

                Digite uma das opções para consultar:
                """;

    System.out.println(menu);
    
    var opcao = leitura.nextLine();

    String endereco; 
        if(opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas"; 
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumoApi.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
            .sorted(Comparator.comparing(Dados::codigo))
            .forEach(System.out::println);


            System.out.println("Informe o código do veículo para consulta: ");
            var codigoMarca = leitura.nextLine();

            endereco = endereco + "/" + codigoMarca + "/modelos"; 
            json = consumoApi.obterDados(endereco);
            var modeloLista = conversor.obterDados(json, Modelos.class);

            System.out.println("Modelos desta marca: ");

            modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

            System.out.println("Digite o carro a ser buscado: ");

            var nomeVeiculo = leitura.nextLine();

            List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                        .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                        .collect(Collectors.toList());
            System.out.println("Modelos Filtrados: ");
            modelosFiltrados.forEach(System.out::println);
            
            System.out.println("Digite por favor o código do modelo desejado: ");
            var codigoModelo = leitura.nextLine();

            endereco = endereco + "/" + codigoModelo + "/anos";

            json = consumoApi.obterDados(endereco);
            List<Dados> anos = conversor.obterLista(json, Dados.class);
            List<Veiculo> veiculos = new ArrayList<>(); 

            for(int i = 0; i < anos.size(); i++) {
                var enderecoAnos = endereco + "/" + anos.get(i).codigo();
                json = consumoApi.obterDados(enderecoAnos);
                Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
                veiculos.add(veiculo);
                
                System.out.println("Todos os veiculos filtrados com avaliações por ano.");
                veiculos.forEach(System.out::println);
            }
    }
}
