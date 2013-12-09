package br.com.phsd.migalhatracker.modelo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import br.com.phsd.migalhatracker.util.Utils;

import com.google.android.gms.maps.model.LatLng;

public class Posicao implements Serializable, Comparable<Posicao> {

	private static final long serialVersionUID = -925202444170106179L;
	
	private Integer id;
	
	private Integer idTrajeto;
	
	private Double latitude;
	
	private Double longitude;
	
	private Date momento;
	
	public Posicao(Cursor c) {
		this.id = c.getInt(c.getColumnIndex("ID"));
		this.idTrajeto = c.getInt(c.getColumnIndex("ID_TRAJETO"));
		this.latitude = c.getDouble(c.getColumnIndex("LATITUDE"));
		this.longitude = c.getDouble(c.getColumnIndex("LONGITUDE"));
		this.momento = Utils.formatStringToDateTime(c.getString(c.getColumnIndex("MOMENTO")));
	}
	
	public Posicao(LatLng latLng, Integer idTrajeto) {
		this.idTrajeto = idTrajeto;
		this.latitude = latLng.latitude;
		this.longitude = latLng.longitude;
		this.momento = Calendar.getInstance().getTime();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Date getMomento() {
		return momento;
	}

	public void setMomento(Date momento) {
		this.momento = momento;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public LatLng toLatLng() {
		return new LatLng(latitude, longitude);
	}
	
	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		
		cv.put("ID_TRAJETO", idTrajeto);
		cv.put("LATITUDE", latitude);
		cv.put("LONGITUDE", longitude);
		cv.put("MOMENTO", getMomentoAsString());
		
		return cv;
	}

	public String getMomentoAsString() {
		return Utils.formatDateToString(momento);
	}

	@Override
	public int compareTo(Posicao another) {
		return this.getMomento().compareTo(another.getMomento());
	}

}
