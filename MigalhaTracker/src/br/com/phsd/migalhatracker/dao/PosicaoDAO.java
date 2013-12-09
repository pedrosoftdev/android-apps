package br.com.phsd.migalhatracker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import br.com.phsd.migalhatracker.modelo.Posicao;

public class PosicaoDAO extends SQLiteDAO {
	
	private String[] COLS = new String[] {"ID", "ID_TRAJETO", "LATITUDE", "LONGITUDE", "MOMENTO"};

	public PosicaoDAO(Context context) {
		super(context);
	}
	
	public void inserir(Posicao posicao) {
		getWritableDatabase().insert("POSICAO", null, posicao.toContentValues());
	}
	
	public List<Posicao> getLista(Integer idTrajeto) {		
		List<Posicao> posicoes = new ArrayList<Posicao>();
		
		Cursor c = null;
		try {
			c = getReadableDatabase().query("POSICAO", COLS, "ID_TRAJETO=?", new String[]{idTrajeto.toString()}, null, null, "MOMENTO ASC");
			
			while (c.moveToNext()) {
				posicoes.add(new Posicao(c));
			}
		} finally {
			c.close();
		}

		return posicoes;
	}
	
	public Posicao getUltimaPosicao(Integer idTrajeto) {
		
		Posicao p = null;
		Cursor c = null;
		
		try {
			c = getReadableDatabase().query("POSICAO", COLS, "ID_TRAJETO=?", new String[]{idTrajeto.toString()}, null, null, "MOMENTO DESC");
			
			if (c.moveToFirst()) {
				p = new Posicao(c);
			}
		} finally {
			c.close();
		}

		return p;
		
	}

}
