package br.edu.projeto.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.sql.DataSource;

import br.edu.projeto.model.Pessoa;
import br.edu.projeto.util.DbUtil;

//Classe DAO responsável pelas regras de negócio envolvendo operações de persistência de dados
//Indica-se a acriação de um DAO específico para cada Modelo

//Anotação EJB que indica que Bean (objeto criado para a classe) será comum para toda a aplicação
//Isso faz com que recursos computacionais sejam otimizados e garante maior segurança nas transações com o banco
@Stateful
public class PessoaDAO implements Serializable{
	private static final long serialVersionUID = 1L;

	@Inject
	private DataSource ds;
    
    public List<Pessoa> listAll(String filtro, String opt) {
    	List<Pessoa> Pessoas = new ArrayList<Pessoa>();
    	Connection con = null;//Conexão com a base
    	PreparedStatement ps = null;//Instrução SQL
    	ResultSet rs = null;//Resposta do SGBD
		String query = "SELECT * FROM Clientes WHERE nome LIKE '%" + filtro + "%'";

		if (opt != null) {
			if (!opt.isEmpty()){
				query += " AND genero = '" + opt + "'" ;
			}
		}
		try {
			con = this.ds.getConnection();//Pegar um conexão
			
			ps = con.prepareStatement(query);	
			
			rs = ps.executeQuery();
		
			while (rs.next()) {//Pega próxima linha do retorno
				Pessoa c = new Pessoa();
				c.setCpf(rs.getString("cpf"));
				c.setNome(rs.getString("nome"));
				c.setNomeSocial(rs.getString("nome_social"));
				c.setIdade(rs.getInt("idade"));
				c.setGenero(rs.getString("genero"));
				c.setAltura(Double.toString(rs.getDouble("altura")));
				c.setMassa(Double.toString(rs.getDouble("massa")));
				c.setEmail(rs.getString("email"));
				c.setTelefoneCelular(rs.getString("telefone_celular"));
				c.setEndereco(rs.getString("endereco"));
				c.setNacionalidade(rs.getInt("nacionalidade_id"));
				Pessoas.add(c);
			}
		} catch (SQLException e) {e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
        return Pessoas;
    }
       
    public Boolean insert(Pessoa c) {
    	Boolean resultado = false;
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
	    	con = this.ds.getConnection();
	    	try {
				ps = con.prepareStatement("INSERT INTO Clientes (CPF, nome, nome_social, altura, massa, genero, idade, email, telefone_celular, endereco, nacionalidade_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				ps.setString(1, c.getCpf());
				ps.setString(2, c.getNome());
				ps.setString(3, c.getNomeSocial());
				ps.setDouble(4, Float.parseFloat(c.getAltura().replace(",", ".")));
				ps.setDouble(5, Float.parseFloat(c.getMassa().replace(",", ".")));
				ps.setString(6, c.getGenero());
				ps.setInt(7, c.getIdade());
				ps.setString(8, c.getEmail());
				ps.setString(9, c.getTelefoneCelular());
				ps.setString(10 , c.getEndereco());
				ps.setInt(11, c.getNacionalidade());
				ps.execute();
				resultado = true;
			} catch (SQLException e) {e.printStackTrace();}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	} finally {
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
    	return resultado;
    }
    
    public Boolean update(Pessoa c) {
    	Boolean resultado = false;
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
	    	con = this.ds.getConnection();
	    	try {				
				ps = con.prepareStatement("UPDATE Clientes SET nome = ?, nome_social = ?, altura = ?, massa = ?, genero = ?, idade = ?, email = ?, telefone_celular = ?, endereco = ?, nacionalidade_id = ? WHERE CPF = ?");
				ps.setString(1, c.getNome());
				ps.setString(2, c.getNomeSocial());
				ps.setDouble(3, Float.parseFloat(c.getAltura().replace(",", ".")));
				ps.setDouble(4, Float.parseFloat(c.getMassa().replace(",", ".")));
				ps.setString(5, c.getGenero());
				ps.setInt(6, c.getIdade());
				ps.setString(7, c.getEmail());
				ps.setString(8, c.getTelefoneCelular());
				ps.setString(9, c.getEndereco());
				ps.setInt(10, c.getNacionalidade());
				ps.setString(11, c.getCpf());
				ps.execute();	
				resultado = true;
			} catch (SQLException e) {e.printStackTrace();}
    	} catch (SQLException e) {e.printStackTrace();
    	} finally {
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
    	return resultado;
    }
    
    public Boolean delete(Pessoa c) {
    	Boolean resultado = false;
    	Connection con = null;
    	PreparedStatement ps = null;
    	try {
	    	con = this.ds.getConnection();
	    	try {				
	    		ps = con.prepareStatement("DELETE FROM Clientes WHERE CPF = ?");
	    		ps.setString(1, c.getCpf());
				ps.execute();
				resultado = true;
			} catch (SQLException e) {e.printStackTrace();}
    	} catch (SQLException e) {e.printStackTrace();
    	} finally {
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
    	return resultado;
    }
}