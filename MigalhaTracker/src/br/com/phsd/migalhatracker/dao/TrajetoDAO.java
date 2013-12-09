package br.com.phsd.migalhatracker.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.phsd.migalhatracker.modelo.Trajeto;
import android.content.Context;
import android.database.Cursor;

public class TrajetoDAO extends SQLiteDAO {
	
	private String[] COLS = new String[] {"ID", "NAVEGANDO", "NOME"};

	public TrajetoDAO(Context context) {
		super(context);
	}
	
	public void inserir(Trajeto trajeto) {
		getWritableDatabase().insert("TRAJETO", null, trajeto.toContentValues());
	}
	
	public void atualizar(Trajeto trajeto) {
		getWritableDatabase().update("TRAJETO", trajeto.toContentValues(), "ID=?", new String[]{trajeto.getId().toString()});
	}
	
	public Trajeto getTrajetoEmNavegacao() {
		Trajeto t = null;
		Cursor c = null;
		
		try {
			c = getReadableDatabase().query("TRAJETO", COLS, "NAVEGANDO=?", new String[]{String.valueOf(Trajeto.NAV_SIM)}, null, null, null);
			
			if (c.moveToFirst()) {
				t = new Trajeto(c);
			}
		} finally {
			c.close();
		}
		
		return t;
	}

	public List<Trajeto> getLista() {
		List<Trajeto> trajetos = new ArrayList<Trajeto>();
		Cursor c = null;
		
		try {
			c = getReadableDatabase().query("TRAJETO", COLS, "NAVEGANDO=?", new String[]{String.valueOf(Trajeto.NAV_NAO)}, null, null, null);
			
			if (c.moveToNext()) {
				trajetos.add(new Trajeto(c));
			}
		} finally {
			c.close();
		}
		
		return trajetos;
	}

	public void apagar(Trajeto trajeto) {
		getWritableDatabase().delete("TRAJETO", "ID=?", new String[]{trajeto.getId().toString()});
	}

}
