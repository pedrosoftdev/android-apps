package br.com.phsd.migalhatracker.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import br.com.phsd.migalhatracker.R;
import br.com.phsd.migalhatracker.dao.PosicaoDAO;
import br.com.phsd.migalhatracker.dao.TrajetoDAO;
import br.com.phsd.migalhatracker.modelo.Posicao;
import br.com.phsd.migalhatracker.modelo.Trajeto;
import br.com.phsd.migalhatracker.util.StatusTrajeto;
import br.com.phsd.migalhatracker.util.Utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity implements LocationListener {

	private GoogleMap map;
	private LocationManager locationManager;
	private String provider;

	private Menu menu;

	private Trajeto trajeto;

	private TrajetoDAO tDAO;
	private PosicaoDAO pDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		validateProvider();

		iniciarBanco();
		
		manterTelaLigada();

		carregarTrajeto();

		onLocationChanged(locationManager.getLastKnownLocation(provider));

	}

	private void manterTelaLigada() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void iniciarBanco() {
		tDAO = new TrajetoDAO(this);
		pDAO = new PosicaoDAO(this);
	}

	private void carregarTrajeto() {

		trajeto = tDAO.getTrajetoEmNavegacao();

		if (trajeto == null) {
			mudarMenu(StatusTrajeto.INERTE);
		} else {
			
			if (trajeto.getNavegando() == Trajeto.NAV_SIM) {
				mudarMenu(StatusTrajeto.NAVEGANDO);
			} else {
				mudarMenu(StatusTrajeto.EXIBINDO);
			}

		}

	}
	
	private void mudarMenu(StatusTrajeto status) {
		
		if (menu == null)
			return;
		
		switch (status) {
		case INERTE:
			
			menu.getItem(0).setVisible(true);
			menu.getItem(1).setVisible(false);
			menu.getItem(2).setVisible(true);
			menu.getItem(3).setVisible(false);
			
			break;

		case NAVEGANDO:
			
			menu.getItem(0).setVisible(false);
			menu.getItem(1).setVisible(true);
			menu.getItem(2).setVisible(false);
			menu.getItem(3).setVisible(false);
			
			break;
			
		case EXIBINDO:
			
			menu.getItem(0).setVisible(false);
			menu.getItem(1).setVisible(false);
			menu.getItem(2).setVisible(true);
			menu.getItem(3).setVisible(true);
			
			break;
		}

	}

	public void validateProvider() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

				AlertDialog.Builder dlgGps = new AlertDialog.Builder(this);

				dlgGps.setTitle(R.string.gps_settings);
				dlgGps.setMessage(R.string.gps_not_enabled);

				dlgGps.setPositiveButton(R.string.settings, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

				dlgGps.setNegativeButton(R.string.cancel, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

						Toast.makeText(MainActivity.this, R.string.gps_exit, Toast.LENGTH_LONG).show();

						finish();
					}
				});

				dlgGps.show();

			} else {
				provider = LocationManager.GPS_PROVIDER;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		validateProvider();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			LatLng latLng = new LatLng(latitude, longitude);

			validarPosicao(latLng);

			map.clear();

			map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.you)));

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

			map.addPolyline(tracarTrajeto(latLng));
		}
	}

	private void validarPosicao(LatLng atual) {
		if (trajeto != null) {

			LatLng ultima = pDAO.getUltimaPosicao(trajeto.getId()).toLatLng();

			// Caso tenha percorrido mais de 10m armazena a posição.
			if (Utils.calcularDistancia(atual, ultima) > 10) {
				pDAO.inserir(new Posicao(atual, trajeto.getId()));
			}

		}
	}

	private PolylineOptions tracarTrajeto(LatLng latLng) {
		PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.BLUE);

		if (trajeto != null) {
			for (Posicao posicao : pDAO.getLista(trajeto.getId())) {
				rectLine.add(posicao.toLatLng());
			}

			rectLine.add(latLng);
		}

		return rectLine;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, R.string.gps_deactivate, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, R.string.gps_activate, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		this.menu = menu;

		menu.add(0, 0, 0, R.string.start_tracking);
		menu.add(0, 1, 0, R.string.finish_tracking).setVisible(false);
		menu.add(0, 2, 0, R.string.view_saved_tracking);
		menu.add(0, 3, 0, R.string.clear_tracking).setVisible(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0:

			iniciar();
			break;

		case 1:

			finalizar();
			break;

		case 2:

			exibir();
			break;
			
		case 3:
			
			limpar();
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	private void iniciar() {
		tDAO.inserir(new Trajeto());

		carregarTrajeto();

		inserirPosicaoAtual();

		Toast.makeText(this, getString(R.string.tracking_started), Toast.LENGTH_LONG).show();
		
		mudarMenu(StatusTrajeto.NAVEGANDO);
	}

	public void inserirPosicaoAtual() {
		Location location = locationManager.getLastKnownLocation(provider);

		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

		pDAO.inserir(new Posicao(latLng, trajeto.getId()));
	}

	private void finalizar() {
		AlertDialog.Builder dlgFinaliza = new AlertDialog.Builder(this);

		dlgFinaliza.setTitle(R.string.finish_tracking);
		dlgFinaliza.setMessage(R.string.finish_question);

		dlgFinaliza.setPositiveButton(R.string.yes, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				AlertDialog.Builder dlgSalvar = new AlertDialog.Builder(MainActivity.this);

				dlgSalvar.setTitle(R.string.finish_tracking);
				dlgSalvar.setMessage(R.string.save_tracking);

				final EditText edtNome = new EditText(MainActivity.this);
				edtNome.setHint(trajeto.getNome());
				dlgSalvar.setView(edtNome);

				dlgSalvar.setPositiveButton(R.string.save, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						trajeto.setNome(edtNome.getEditableText().toString());
						trajeto.finalizar();
						
						tDAO.atualizar(trajeto);

						carregarTrajeto();

						onLocationChanged(locationManager.getLastKnownLocation(provider));
					}
				});

				dlgSalvar.setNegativeButton(R.string.cancel, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						tDAO.apagar(trajeto);
						
						carregarTrajeto();

						onLocationChanged(locationManager.getLastKnownLocation(provider));
					}
				});

				mudarMenu(StatusTrajeto.INERTE);
				
				dlgSalvar.show();

				dialog.cancel();
			}
		});

		dlgFinaliza.setNegativeButton(R.string.cancel, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		dlgFinaliza.show();
	}

	private void exibir() {

		AlertDialog.Builder dlgExibir = new AlertDialog.Builder(this);
		
		dlgExibir.setTitle(R.string.select_saved_tracking);

		final ArrayAdapter<Trajeto> trajetos = carregarTrajetos();
		
		dlgExibir.setAdapter(trajetos, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				trajeto = trajetos.getItem(which);
				
				onLocationChanged(locationManager.getLastKnownLocation(provider));
				
				Toast.makeText(MainActivity.this, R.string.saved_tracking_selected, Toast.LENGTH_LONG).show();
				
				mudarMenu(StatusTrajeto.EXIBINDO);
			}
		});
		
		dlgExibir.show();
		
	}

	private ArrayAdapter<Trajeto> carregarTrajetos() {
		return new ArrayAdapter<Trajeto>(this, android.R.layout.simple_list_item_1, tDAO.getLista());
	}
	
	private void limpar() {
		trajeto = null;
		
		onLocationChanged(locationManager.getLastKnownLocation(provider));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		tDAO.close();
		pDAO.close();
	}

}
