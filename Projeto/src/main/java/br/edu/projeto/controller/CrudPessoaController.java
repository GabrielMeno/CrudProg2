package br.edu.projeto.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.edu.projeto.dao.PessoaDAO;
import br.edu.projeto.dao.NacionalidadeDAO;
import br.edu.projeto.model.Pessoa;
import br.edu.projeto.model.Nacionalidades;

//Escopo do objeto da classe (Bean)
//ApplicationScoped é usado quando o objeto é único na aplicação (compartilhado entre usuários), permanece ativo enquanto a aplicação estiver ativa
//SessionScoped é usado quando o objeto é único por usuário, permanece ativo enquanto a sessão for ativa
//ViewScoped é usado quando o objeto permanece ativo enquanto não houver um redirect (acesso a nova página)
//RequestScoped é usado quando o objeto só existe naquela requisição específica
//Quanto maior o escopo, maior o uso de memória no lado do servidor (objeto permanece ativo por mais tempo)
//Escopos maiores que Request exigem que classes sejam serializáveis assim como todos os seus atributos (recurso de segurança)
@ViewScoped
//Torna classe disponível na camada de visão (html) - são chamados de Beans ou ManagedBeans (gerenciados pelo JSF/EJB)
@Named
public class CrudPessoaController implements Serializable {
	private static final long serialVersionUID = 1L;

	//Anotação que marca atributo para ser gerenciado pelo CDI
	//O CDI criará uma instância do objeto automaticamente quando necessário
	@Inject
	private FacesContext facesContext;
	
	@Inject
    private PessoaDAO PessoaDAO;
	@Inject
	private NacionalidadeDAO NacionalidadeDAO;

	private Pessoa pessoa;
	
	private List<Pessoa> listaPessoa;
	private List<Nacionalidades> listaNac1;
	private Map<String, String> listaNac = new HashMap<>();
	
	private Boolean rendNovoCadastro;
	
	private String filtro;
	private String selectedOption;	
	//Anotação que força execução do método após o construtor da classe ser executado
    @PostConstruct
    public void init() {
    	//Inicializa elementos importantes
    	listaNac = new HashMap<>();
    	this.setListaNac1(NacionalidadeDAO.listAll());
    	
    	for(int i = 0; i < listaNac1.size(); i++ ) {
    		listaNac.put(listaNac1.get(i).getDesc(), listaNac1.get(i).getId().toString());
    	}
    	
    	this.setListaPessoa(PessoaDAO.listAll("",""));
    }

	public Map<String, String> getListaNac() {
		return listaNac;
	}

	public void setListaNac(Map<String, String> nacGroup) {
		this.listaNac = nacGroup;
	}
    
	public List<Nacionalidades> getListaNac1() {
		return listaNac1;
	}

	public void setListaNac1(List<Nacionalidades> listaNac) {
		this.listaNac1 = listaNac;
	}
    
	public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
    
    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
	
    //Chamado pelo botão novo
	public void novoCadastro() {
        this.setPessoa(new Pessoa());
        this.setRendNovoCadastro(true);
    }
	
	//Chamado pelo botão alterar
	public void alterarCadastro() {
        this.setRendNovoCadastro(false);
    }
	
	//Chamado pelo botão remover da tabela
	public void remover() {
		if (this.PessoaDAO.delete(this.pessoa)) {
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Pessoa Removida", null));
			this.listaPessoa.remove(this.pessoa);
		} else 
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao Remover Pessoa", null));
		//Após excluir usuário é necessário recarregar lista que popula tabela com os novos dados
		//this.listaPessoa = PessoaDAO.listAll();
        //Limpa seleção de usuário
		this.pessoa = null;
        PrimeFaces.current().ajax().update("form:messages", "form:dt-pessoas");
	}	
	
	//Chamado ao salvar cadastro de usuário (novo)
	public void salvarNovo() {
		if (ValidarDados().equals("")) {
			if (this.PessoaDAO.insert(this.pessoa)) {
				this.getListaPessoa().add(this.pessoa);
				PrimeFaces.current().executeScript("PF('pessoaDialog').hide()");
				PrimeFaces.current().ajax().update("form:dt-pessoa");
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Pessoa Criada", null));
			} else {
	        	this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao Criar Pessoa", null));
			}
		} else {
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ValidarDados(), null));
		}
		PrimeFaces.current().ajax().update("form:messages");
	}
	
	//Chamado ao salvar cadastro de usuário (alteracao)
	public void salvarAlteracao() {
		if (ValidarDados().equals("")) {
			if (this.PessoaDAO.update(this.pessoa)) {
				PrimeFaces.current().executeScript("PF('pessoaDialog').hide()");
				PrimeFaces.current().ajax().update("form:dt-pessoa");
				this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Pessoa Atualizada", null));
			} else
		    	this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao Atualizar Pessoa", null));
		} else {
			this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ValidarDados(), null));
		}
		this.setListaPessoa(PessoaDAO.listAll("",""));
		PrimeFaces.current().ajax().update("form:messages");
	}
	
	//GETs e SETs
	//GETs são necessários para elementos visíveis em tela
	//SETs são necessários para elementos que serão editados em tela
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public List<Pessoa> getListaPessoa() {
		return listaPessoa;
	}

	public void setListaPessoa(List<Pessoa> listaPessoa) {
		this.listaPessoa = listaPessoa;
	}

	public Boolean getRendNovoCadastro() {
		return rendNovoCadastro;
	}

	public void setRendNovoCadastro(Boolean rendNovoCadastro) {
		this.rendNovoCadastro = rendNovoCadastro;
	}

	public String ValidarDados(){
		String massa = this.pessoa.getMassa();
		String altura = this.pessoa.getAltura();

		// Validar campo Genero
		if (!this.pessoa.getGenero().toLowerCase().equals("m") && !this.pessoa.getGenero().toLowerCase().equals("f") && !this.pessoa.getGenero().toLowerCase().equals("o")){
			return "Genero deve ser M, F ou O";
		}
		// Validar campo Massa
		if (!massa.matches("^[0-9]+([,.][0-9]{1,2})?$")) {
			return "Campo massa está preenchido incorretamente";
		}
		// Validar campo Altura
		if (!altura.matches("^[0-9]+([,.][0-9]{1,2})?$")) {
			return "Campo altura está preenchido incorretamente";
		}

		return "";
		  
	}
	
	public void FiltrarBrowse() {
		this.setListaPessoa(PessoaDAO.listAll(filtro, selectedOption));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-pessoas");
	}

}