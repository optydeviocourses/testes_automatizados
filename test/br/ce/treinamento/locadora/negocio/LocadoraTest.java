package br.ce.treinamento.locadora.negocio;

import static br.ce.treinamento.matchers.MatchersProprios.mesmoDiaQue;
import static br.ce.treinamento.util.DataUtil.obterDataZerada;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.ce.treinamento.locadora.entidades.Filme;
import br.ce.treinamento.locadora.entidades.Locacao;
import br.ce.treinamento.locadora.entidades.Usuario;
import br.ce.treinamento.locadora.exceptions.LocadoraException;
import br.ce.treinamento.matchers.MatchersProprios;
import br.ce.treinamento.util.DataUtil;

public class LocadoraTest {

	private Locadora locadora;
	private Usuario usuario;
	private List<Filme> filmes;
	
	@Rule
	public ExpectedException excecaoEsperada = ExpectedException.none();
	
	@Before
	public void setup(){
		locadora = new Locadora();
		usuario = new Usuario("Joseh");
		filmes = new ArrayList<Filme>();
	}
	
	@Test
	public void deveTerDataRetornoAmanhaAoAlugarFilme() throws LocadoraException{
		//Cenario
		Filme filme = new Filme("GodFather", 5, 5.0);
		filmes.add(filme);
		
		//Acao
		Locacao locacao = locadora.alugarFilme(usuario, filmes);
		
		//Validacao
		Calendar calendarDataRetorno = Calendar.getInstance();
		calendarDataRetorno.setTime(locacao.getDataRetorno());
		
		Calendar dataRetornoEsperada = Calendar.getInstance();
		dataRetornoEsperada.add(Calendar.DAY_OF_MONTH, 1);
		
		Assert.assertThat(calendarDataRetorno, is(mesmoDiaQue(dataRetornoEsperada)));
	}
	
	@Test
	public void deveCalcularPrecoAoAlugarFilme() throws LocadoraException{
		//Cenario
		Filme filme = new Filme("GodFather", 5, 5.0);
		filmes.add(filme);
		
		//Acao
		Locacao locacao = locadora.alugarFilme(usuario, filmes);
		
		//Validacao
		Assert.assertThat(locacao.getValor(), is(closeTo(5.0, 0.001)));
	}
	
	@Test
	public void deveReduzirEstoqueAoAlugarFilme() throws LocadoraException {
		//Cenario
		Filme filme = new Filme("E o tempo levou", 3, 1.50);
		filmes.add(filme);
		
		//Acao
		Locacao locacao = locadora.alugarFilme(usuario, filmes);
		
		//Verificacao
		Assert.assertThat(locacao.getFilmes().get(0).getEstoque().intValue(), is(2));
	}
	
	@Test
	public void deveLancarExcecaoQuandoAlugarFilmeComEstoqueMinimo() {
		//Cenario
		Filme filme = new Filme("Matrix", 0, 4.0);
		filmes.add(filme);
		
		//Acao
		try {
			locadora.alugarFilme(usuario, filmes);
			
		//Verificacao
			Assert.fail("Uma excecao deveria ter sido lancada na linha anterior");
		} catch (LocadoraException e) {
//			Assert.assertEquals("Nao eh possivel alugar filme que nao estah no estoque", e.getMessage());
			Assert.assertThat(e.getMessage(), is(equalTo("Nao eh possivel alugar filme que nao estah no estoque")));
		}
	}
	
	@Test
	public void deveLancarExcecaoQuandoAlugarFilmeSemFilmeDefinido() throws LocadoraException{
		//Cenario
		excecaoEsperada.expect(LocadoraException.class);
		excecaoEsperada.expectMessage("O Filme nao pode estar vazio");
		
		//Acao
		locadora.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveAdicionarUmDiaNaEntregaAoAlugar4Filmes() throws LocadoraException {
		Assume.assumeFalse(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY);
		
		//Cenario
		Filme filme1 = new Filme("Iron Man", 2, 4.0);
		Filme filme2 = new Filme("Capitao America", 1, 3.0);
		Filme filme3 = new Filme("Thor", 1, 2.50);
		Filme filme4 = new Filme("The Avengers", 10, 5.0);
		filmes = Arrays.asList(filme1, filme2, filme3, filme4);
		
		Locacao locacao = locadora.alugarFilme(usuario, filmes);
		
		Calendar dataEsperada = Calendar.getInstance();
		dataEsperada.add(Calendar.DAY_OF_MONTH, 2);
		
		Calendar dataRetorno = Calendar.getInstance();
		dataRetorno.setTime(locacao.getDataRetorno());
		
		Assert.assertThat(dataEsperada.get(Calendar.DAY_OF_MONTH), is(equalTo(dataRetorno.get(Calendar.DAY_OF_MONTH))));
	}
	
	@Test
	public void deveEntregarFilmeNaSegundaAoAlugar4FilmesNaSexta() throws LocadoraException {
		Assume.assumeTrue(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY);
		
		//Cenario
		Filme filme1 = new Filme("Iron Man", 2, 4.0);
		Filme filme2 = new Filme("Capitao America", 1, 3.0);
		Filme filme3 = new Filme("Thor", 1, 2.50);
		Filme filme4 = new Filme("The Avengers", 10, 5.0);
		filmes = Arrays.asList(filme1, filme2, filme3, filme4);
		
		Locacao locacao = locadora.alugarFilme(usuario, filmes);
		
		Calendar dataEsperada = Calendar.getInstance();
		dataEsperada.add(Calendar.DAY_OF_MONTH, 3);
		
		Calendar dataRetorno = Calendar.getInstance();
		dataRetorno.setTime(locacao.getDataRetorno());
		
		Assert.assertThat(dataEsperada.get(Calendar.DAY_OF_MONTH), is(equalTo(dataRetorno.get(Calendar.DAY_OF_MONTH))));
	}
}
