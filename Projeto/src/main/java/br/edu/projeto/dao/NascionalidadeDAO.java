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

import br.edu.projeto.model.Nacionalidades;
import br.edu.projeto.util.DbUtil;

//Classe DAO responsável pelas regras de negócio envolvendo operações de persistência de dados
//Indica-se a acriação de um DAO específico para cada Modelo

//Anotação EJB que indica que Bean (objeto criado para a classe) será comum para toda a aplicação
//Isso faz com que recursos computacionais sejam otimizados e garante maior segurança nas transações com o banco
@Stateful
public class NacionalidadeDAO implements Serializable{
	private static final long serialVersionUID = 1L;

	@Inject
	private DataSource ds;
    
    public List<Nacionalidades> listAll() {
    	List<Nacionalidades> nacionalidades = new ArrayList<Nacionalidades>();
    	Connection con = null;//Conexão com a base
    	PreparedStatement ps = null;//Instrução SQL
    	ResultSet rs = null;//Resposta do SGBD
		String query = "SELECT * FROM Nacionalidades";

		try {
			con = this.ds.getConnection();//Pegar um conexão
			
			ps = con.prepareStatement(query);	
			
			rs = ps.executeQuery();
		
			while (rs.next()) {//Pega próxima linha do retorno
				Nacionalidades c = new Nacionalidades();
				c.setId(rs.getInt("id"));
				c.setDesc(rs.getString("tipo_nacionalidade"));
				nacionalidades.add(c);
			}
		} catch (SQLException e) {e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closePreparedStatement(ps);
			DbUtil.closeConnection(con);
		}
        return nacionalidades;
    }
}