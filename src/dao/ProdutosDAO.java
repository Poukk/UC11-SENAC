package dao;

import dto.ProdutosDTO;
import java.sql.PreparedStatement;
import java.sql.Connection;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProdutosDAO {
    Connection conn;
    PreparedStatement prep;
    ResultSet resultset;
    
    public boolean cadastrarProduto(ProdutosDTO produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nome do produto é obrigatório!");
            return false;
        }
        if (produto.getValor() == null || produto.getValor() <= 0) {
            JOptionPane.showMessageDialog(null, "Valor do produto deve ser maior que zero!");
            return false;
        }
        
        String sql = "INSERT INTO produtos (nome, valor, status) VALUES (?, ?, ?)";
        conn = new conectaDAO().connectDB();
        
        try {
            prep = conn.prepareStatement(sql);
            prep.setString(1, produto.getNome());
            prep.setInt(2, produto.getValor());
            prep.setString(3, produto.getStatus());
            prep.executeUpdate();
            JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
            return true;
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar produto: " + erro.getMessage());
            return false;
        } finally {
            try {
                if (prep != null) prep.close();
                if (conn != null) conn.close();
            } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null, "Erro ao fechar conexão: " + erro.getMessage());
            }
        }
    }
    
    public ArrayList<ProdutosDTO> listarProdutos() {
        String sql = "SELECT * FROM produtos";
        conn = new conectaDAO().connectDB();
        ArrayList<ProdutosDTO> listagem = new ArrayList<>();
        
        try {
            prep = conn.prepareStatement(sql);
            resultset = prep.executeQuery();
            
            while (resultset.next()) {
                ProdutosDTO produto = new ProdutosDTO();
                produto.setId(resultset.getInt("id"));
                produto.setNome(resultset.getString("nome"));
                produto.setValor(resultset.getInt("valor"));
                produto.setStatus(resultset.getString("status"));
                listagem.add(produto);
            }
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Erro ProdutosDAO listar: " + erro.getMessage());
        } finally {
            try {
                if (resultset != null) resultset.close();
                if (prep != null) prep.close();
                if (conn != null) conn.close();
            } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null, "Erro ao fechar conexão: " + erro.getMessage());
            }
        }
        
        return listagem;
    }
    
    public boolean venderProduto(int id) {
        String sqlCheck = "SELECT status FROM produtos WHERE id = ?";
        String sqlUpdate = "UPDATE produtos SET status = 'Vendido' WHERE id = ? AND status = 'A Venda'";
        conn = new conectaDAO().connectDB();
        
        try {
            // Check if product exists and is available for sale
            prep = conn.prepareStatement(sqlCheck);
            prep.setInt(1, id);
            resultset = prep.executeQuery();
            
            if (!resultset.next()) {
                JOptionPane.showMessageDialog(null, "Produto com ID " + id + " não encontrado!");
                return false;
            }
            
            String status = resultset.getString("status");
            if (!"A Venda".equals(status)) {
                JOptionPane.showMessageDialog(null, "Produto já foi vendido ou não está disponível!");
                return false;
            }
            
            // Update product status to sold
            prep = conn.prepareStatement(sqlUpdate);
            prep.setInt(1, id);
            int rowsAffected = prep.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Produto vendido com sucesso!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao vender produto!");
                return false;
            }
            
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Erro ao vender produto: " + erro.getMessage());
            return false;
        } finally {
            try {
                if (resultset != null) resultset.close();
                if (prep != null) prep.close();
                if (conn != null) conn.close();
            } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null, "Erro ao fechar conexão: " + erro.getMessage());
            }
        }
    }
}
