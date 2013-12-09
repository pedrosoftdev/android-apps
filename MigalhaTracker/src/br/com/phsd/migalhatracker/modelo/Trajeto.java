package br.com.phsd.migalhatracker.modelo;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;

public class Trajeto implements Serializable {

	private static final long serialVersionUID = -5906481197533322246L;
	
	private Integer id;
	
	private char navegando;
	
	private String nome;
	
	public static char NAV_SIM = '1';
	public static char NAV_NAO = '0';
	
	public Trajeto(Cursor c) {
		this.id = c.getInt(c.getColumnIndex("ID"));
		this.navegando = c.getString(c.getColumnIndex("NAVEGANDO")).charAt(0);
		this.nome = c.getString(c.getColumnIndex("NOME"));
	}
	
	public Trajeto() {
		this.navegando = NAV_SIM;
		this.nome = "Novo Trajeto";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public char getNavegando() {
		return navegando;
	}

	public void setNavegando(char navegando) {
		this.navegando = navegando;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		
		cv.put("navegando", String.valueOf(navegando));
		cv.put("NOME", nome);
		
		return cv;
	}

	public void finalizar() {
		this.navegando = NAV_NAO;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}

}
