package ebay;

import java.util.ArrayList;
import java.util.List;

import ebay.exception.EmailJaRegistradoException;
import ebay.exception.EntityNotFoundException;
import ebay.exception.LoginJaRegistradoException;
import ebay.exception.NomeJaRegistradoException;
import ebay.exception.PontuacaoInvalidaException;
import ebay.exception.PublicacaoNaoExisteException;
import ebay.exception.PublicacaoSemFotoException;
import ebay.exception.UsuarioJaAvaliouException;
import ebay.exception.UsuarioNaoPodeAvaliarSuasPublicacoesException;
import ebay.modelo.Avaliacao;
import ebay.modelo.Compra;
import ebay.modelo.Dinheiro;
import ebay.modelo.Foto;
import ebay.modelo.ItemVenda;
import ebay.modelo.Produto;
import ebay.modelo.Publicacao;
import ebay.modelo.Usuario;
import ebay.persistencia.AvaliacaoDAO;
import ebay.persistencia.CompraDAO;
import ebay.persistencia.FotoDAO;
import ebay.persistencia.ItemVendaDAO;
import ebay.persistencia.ProdutoDAO;
import ebay.persistencia.PublicacaoDAO;
import ebay.persistencia.UsuarioDAO;

public class MainEbay {
	public static void main(String[] args) throws Exception {

		UsuarioDAO usuarioDao = new UsuarioDAO();
		PublicacaoDAO publicacaoDao = new PublicacaoDAO();
		AvaliacaoDAO avaliacaoDao = new AvaliacaoDAO();
		FotoDAO fotoDao = new FotoDAO();
		ProdutoDAO produtoDao = new ProdutoDAO();
		ItemVendaDAO itemVendaDao = new ItemVendaDAO();
		CompraDAO compraDao = new CompraDAO();

		// Cria um usuario insere no banco de dados
		Usuario usuario1 = new Usuario("Joilson", "joilson@gmail.com", "joilson", "password", "Almirante Barroso");
		System.out.println(usuario1.isTransient());
		

		// insere o usuário no banco de dados
		usuarioDao.save(usuario1);
		System.out.println(usuario1.isPersistent());
		
		// Faz os testes paara Unique
		try {
			Usuario usuario2 = new Usuario("Joilson", "teste@gmail.com", "teste", "senhateste", "Almirante Barroso");
			usuarioDao.save(usuario2);
		} catch (NomeJaRegistradoException e) {
			System.out.println("Exception: login ja registrado");
		}

		try {
			Usuario usuario2 = new Usuario("teste", "joilson@gmail.com", "teste", "senhateste", "Almirante Barroso");
			usuarioDao.save(usuario2);
		} catch (EmailJaRegistradoException e) {
			System.out.println("Exception: email ja registrado");
		}

		try {
			Usuario usuario2 = new Usuario("teste", "teste@gmail.com", "joilson", "senhateste", "Almirante Barroso");
			usuarioDao.save(usuario2);
		} catch (LoginJaRegistradoException e) {
			System.out.println("Exception: login ja registrado");
		}
		
		List<Foto> fotos = new ArrayList<>();
		// Tenta fazer publicacao sem foto(é obrigatório colocar pelo menos uma foto)
		try {
		usuario1.publicar(
				new Publicacao(usuario1, new Dinheiro(967,12), 100, 
				"Smartphone Motorola Moto G6 Play XT1922 Ouro com 32GB, Tela de 5.7''"+
				", Dual Chip, Android 8.0, 4G, Câmera 13MP, Processador Octa-Core e 3GB de RAM",
				new Produto("Motorola Moto G6", "Telefone"), fotos));
		} catch (PublicacaoSemFotoException e) {
			System.out.println("Exception: publicação sem foto.");
		}
		
		// OBS: sim, eu sei, ao inserir fotos no banco é melhor inserir o (caminho/endereço/diretório/link)
		// As fotos foram inseridas assim apenas para usar um "tipo" diferente no banco
		// Muita mão para adicionar uma foto pelo File
		// Então criei 5 fotos em branco("Isto é um teste, favor não levar para o coração. =* ")
		for(int i = 0; i < 5; i++) {
			byte[] b = new byte[16];
			Foto f = new Foto(b);
			fotos.add(f);
		}
	
		
		// cria uma publicação, produto e List de fotos transient
		usuario1.publicar(
				new Publicacao(usuario1, new Dinheiro(967,12), 100, 
				"Smartphone Motorola Moto G6 Play XT1922 Ouro com 32GB, Tela de 5.7''"+
				", Dual Chip, Android 8.0, 4G, Câmera 13MP, Processador Octa-Core e 3GB de RAM",
				new Produto("Motorola Moto G6", "Telefone"), fotos));
		
		// Produto transient
		System.out.println(usuario1.getPublicacoes().get(0).getProduto().isTransient());
		
		// Publicação transient
		System.out.println(usuario1.getPublicacoes().get(0).isTransient());
		
		// Fotos também são transient
		for(Foto f : usuario1.getPublicacoes().get(0).getFotos()) {
			System.out.println(f.isTransient());
		}
		
		// Um usuário pode comprar dele mesmo, mas nesse caso da erro pois 
		// a publicação ainda não existe (não foi inserida no bando de dados)
		try {
			usuario1.comprar(usuario1.getPublicacoes().get(0), 30);
		} catch (PublicacaoNaoExisteException e) {
			System.out.println("Exception: publicação não existe.");
		}
		
		// Inserindo produto no banco de dados
		produtoDao.save(usuario1.getPublicacoes().get(0).getProduto());
		System.out.println(usuario1.getPublicacoes().get(0).getProduto().isPersistent());
		
		// Inserindo publicação no banco de dados
		publicacaoDao.save(usuario1.getPublicacoes().get(0));
		System.out.println(usuario1.getPublicacoes().get(0).isPersistent());
		
		// Inserindo fotos no banco de dados
		for(Foto f : usuario1.getPublicacoes().get(0).getFotos()) {
			fotoDao.save(f);
			System.out.println(f.isPersistent());
		}
		
		// teste para mostrar que a trigger funciona
		// A publicação tem 100 itens e aqui tenta comprar 150
		
		try {
			usuario1.comprar(usuario1.getPublicacoes().get(0), 150);
			usuario1.getCompras().get(0).realizar();
		} catch(Exception e) {
			
			System.out.println("Exception: trigger");
		}
		
		// Partiu casos normais
		
		Usuario usuario2 = new Usuario("Regis", "regis@gmail.com", "reinaldo", "azevedo", "brazil com z");
		System.out.println(usuario2.isTransient());
		
		usuarioDao.save(usuario2);
		System.out.println(usuario2.isPersistent());
		
		usuario2.publicar(new Publicacao(usuario2, new Dinheiro(79,99), 1000, 
				"RITALINA 420MG 60 CÁPSULAS", 
				new Produto("RITALINA", "Medicamento"), fotos));
		
		System.out.println(usuario2.getPublicacoes().get(0).getProduto().isTransient());
		System.out.println(usuario2.getPublicacoes().get(0).isTransient());
		
		produtoDao.save(usuario2.getPublicacoes().get(0).getProduto());
		
		publicacaoDao.save(usuario2.getPublicacoes().get(0));
		System.out.println(usuario2.getPublicacoes().get(0).isPersistent());
		
		for(Foto f : usuario2.getPublicacoes().get(0).getFotos()) {
			fotoDao.save(f);
			System.out.println(f.isPersistent());
		}
		
		// Fazendo compras
		usuario2.comprar(usuario2.getPublicacoes().get(0), 150);
		System.out.println(usuario2.getCompras().get(0).getItemVenda().get(0).getQuantidade() == 150);
		
		// Mudando a quantidade de produto
		usuario2.getCompras().get(0).adicionarCompra(usuario2.getPublicacoes().get(0), 50);
		System.out.println(usuario2.getCompras().get(0).getItemVenda().get(0).getQuantidade() == 200);
		
		// Adicionar outro item nas compras
		usuario2.getCompras().get(0).adicionarCompra(usuario1.getPublicacoes().get(0), 3);
		System.out.println(usuario2.getCompras().get(0).getItemVenda().size() == 2); // 2 itens
		
		// Removendo item das compras
		usuario2.getCompras().get(0).removerCompra(usuario1.getPublicacoes().get(0));
		System.out.println(usuario2.getCompras().get(0).getItemVenda().size() == 1); // 1 item
		
		// Quando realiza uma compra, insere uma compra no banco de dados
		// e diversos itensVenda (numero de publicações diferentes compradas,
		// "não a quantidade de produtos")
		usuario2.getCompras().get(0).realizar();
		
		// Fazendo mudanças nos Usuarios, publicações
		System.out.println(usuario1.getEmail().equals("joilson@gmail.com"));
		usuario1.alterarEmail("joilson_rg@hotmail.com");
		System.out.println(usuario1.getEmail().equals("joilson_rg@hotmail.com"));

		usuarioDao.save(usuario1); // fez update
		
		Usuario usuario1Select = usuarioDao.find(1); // método find
		
		System.out.println(usuario1Select.getLogin().equals(usuario1.getLogin()));
		System.out.println(usuario1);
		System.out.println(usuario1Select); // são iguais
		
		// usuario não existe
		try {
			Usuario temp = usuarioDao.load(500); // método found
		} catch (EntityNotFoundException e) {
			System.out.println("Exception: Entity Not Found");
		}
		
		List<Compra> compras = compraDao.loadCompras(usuario2.getId()); // compra do usuario 2
		
		System.out.println(compras.get(0).getUsuario().getNome().equals(("Regis")));
		
		usuario1.getPublicacoes().get(0).ajustarValor(new Dinheiro(1000, 99));;
		System.out.println(usuario1.getPublicacoes().get(0).getValor().toString().equals(new Dinheiro(1000, 99).toString()));
		publicacaoDao.save(usuario1.getPublicacoes().get(0)); // update
		
		// usuário não pode avaliar suar próprias publicações
		try {
			usuario1.avaliar(usuario1.getPublicacoes().get(0), 2);
		} catch (UsuarioNaoPodeAvaliarSuasPublicacoesException e) {
			System.out.println("Exception: Não é possível avaliar publicações próprias.");
		}
		
		// Não pode dar menos de 1 ponto ou mais de 5 pontos
		// se for avalias tem que dar no mínimo 1 ponto
		try {
			usuario1.avaliar(usuario2.getPublicacoes().get(0), 0);
		} catch(PontuacaoInvalidaException e) {
			System.out.println("Exception: Pontuação inválida");
		}
		
		usuario2.comprar(usuario1.getPublicacoes().get(0), 1);
		usuario2.getCompras().get(1).realizar();
		
		usuario2.avaliar(usuario1.getPublicacoes().get(0), 4);
		avaliacaoDao.save(usuario2.getAvaliacoes().get(0));
		
		try {
			usuario2.avaliar(usuario1.getPublicacoes().get(0), 4);
		} catch (UsuarioJaAvaliouException e) {
			System.out.println("Exception: Usuario já avaliou está publicação.");
		}
		
		// Chamando dados do banco
		List<Compra> compra = compraDao.loadAll();
		List<Avaliacao> avaliacoes = avaliacaoDao.loadAll();
		List<Usuario> usuarios = usuarioDao.loadAll();
		List<Publicacao> publicacoes = publicacaoDao.loadAll();
		List<ItemVenda> itensVenda = itemVendaDao.loadAll();
		List<Produto> produtos = produtoDao.loadAll();
		// load das fotos por publicacao
		List<Foto> fotoss = fotoDao.loadFotos(usuario1.getPublicacoes().get(0).getId());
		
		// load das avaliações dos usuarios
		List<Avaliacao> avalia = avaliacaoDao.getAvaliacoesUsuario(usuario2.getId());

		// load das avaliações das publicacoes
		List<Avaliacao> avalia2 = avaliacaoDao.getAvaliacoesPublicacao(usuario1.getPublicacoes().get(0).getId());
		
		for(Compra c : compra) {
			System.out.println(c);
		}
		
		for(Avaliacao a : avaliacoes) {
			System.out.println(a);
		}
		
		for(Usuario u : usuarios) {
			System.out.println(u);
		}
		
		for(Publicacao p : publicacoes) {
			System.out.println(p);
		}
		
		for(ItemVenda iv : itensVenda) {
			System.out.println(iv);
		}
		
		for(Produto prod : produtos) {
			System.out.println(prod);
		}
		
		for(Foto f : fotoss) {
			System.out.println("id da foto: " + f.getId() + "Id da publicacao" + f.getPublicacao().getId());
		}
		
		for(Avaliacao a : avalia) {
			System.out.println(a);
		}
		
		for(Avaliacao a : avalia2) {
			System.out.println(a);
		}
	}
}