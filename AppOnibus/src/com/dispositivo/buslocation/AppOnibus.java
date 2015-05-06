package com.dispositivo.buslocation;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 *Projeto Integrador Buslocation-interface que coletará dados do GPS android que ficará no onibus.
 *e enviará para um Banco de dados .Projetado está sendo desenvolvido na IDE-Eclipse Juno com 
 *integração do SDK Android.
 *
 */



/*
 *Class AppOnibus 
 * 
 *a classe AppOnibus está herdando uma Activity,ou seja a classe 
 *AppOnibus está herdando características da classe Activity,métodos e atributos.como o Oncreate.
*/


public class AppOnibus extends Activity {
	/*
	 * Definimos os atributos que utilizaremos, depois instânciamos eles no
	 * onCreate para que cada atributo responda a um widget específico que foi
	 * criado no arquivo activity_app_onibus.xml que está na pasta res/layout.
	 */
	Button btnConectar;
	Button btnDesconectar;
	TextView txtLatitude;
	TextView txtLongitude;
	TextView txtVelocidade;
	TextView txtProvedor;
	MinhaThread mThread;
	ProgressDialog mDialog;
	Handler mHandler;
	int i;
	
/*
 * metodo Oncreate
 * 
 * Todo aplicativo android começa por uma Activity.Ou seja, quando uma aplicação android é executada, 
 * na verdade é a sua Activity principal que é lançada.Aqui
 * 
 * O método onCreate() é chamado pelo sistema Android, quando a Activity é iniciada. É aqui que devem ser
 * feitas as inicializações e setada a interface que foi criada no arquivo activity_app_onibus.xml que está 
 * na pasta res/layout . 
 *
 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_onibus);//exibe o layout que foi configuarado no arquivo activity_app_onibus.xml que  									está na pasta res/layout . 

		// buscando os componentes(widgets) da classe R.

		txtLatitude = (TextView) findViewById(R.id.txtLatitude);
		txtLongitude = (TextView) findViewById(R.id.txtLongitude);
		txtProvedor = (TextView) findViewById(R.id.txtProvedor);
		// txtVelocidade=(TextView)findViewById(R.id.txtVelocidade);
		btnConectar = (Button) findViewById(R.id.btnConectar);
		btnDesconectar = (Button) findViewById(R.id.btnDesconectar);


		
		/*
		 * Botão Conectar(btnConectar)
		 * 
		 * Ao clicar no botão conectar,ele irá executar os métodos
		 * iniciarServiço e ativaThread
		 */

		btnConectar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				IniciarServico();
				ativaThread();
				
				/*
				 * Classe dbMysql
				 * 
				 * Instanciando um objeto da classe dbMySql para usar seus atributos e métodos
				 * não estudei nada de mysql a classe dbMysql eu vi em um fórum  uma postagem
				 * de um membro mostrando uma  alternativa em fazer conexão com o mysql direto sem 
				 * necessidade de utilizar webservices,httpclient,e sockets
				 * 
				 */
//			OBS estava tentando conectar em um bd local
				
				dbMySQL bancoDeDados=new dbMySQL();
			    bancoDeDados.conectarMySQL("locahost", "3306", "buslocation", "root", "root");//inserir configurações do BD para conexão
			    bancoDeDados.queryMySQL("INSERT INTO posicao(latitude,longitude)VALUES('123434','334444')");//passar como parâmetro consulta sql
				
			    // msg();

			}
		});

		/*
		 * Botão Desconectar(btnDesconectar)
		 * 
		 * Ao clicar no botão desconectar,ele irá executar o método
		 * PararServiço finilizando a aplicação.
		 */
		
		btnDesconectar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				PararServico();

			}
		});

	}

	
	
	public void IniciarServico() {
		// método que que inicia o serviço de coleta de dados do GPS
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {
			/*
			 * Toda vez que a posição no android for modificada durante as
			 * consultas,este método é acionado. Perceba que recebemos como
			 * parâmetro uma classe do tipo Location 
			 * onde  nela contém a latitude e longitude
			 */

			public void onLocationChanged(Location location) {
				// chamada do método atualizar
				Atualizar(location);

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
			
			public void onProviderEnabled(String provider) {
			}
			/*
			 *Método OnProviderDisabled
			 * 
			 *este método irá verificar se o provedor GPS está habilitado,caso não esteja
			 *direcionará o usuário	para tela de config. do sistema para que ele possa habilitar o
			 *GPS(OBS....Estudar Intents)	 
				
			*/
			public void onProviderDisabled(String provider) {
				if (provider.equals(LocationManager.GPS_PROVIDER)) {
					Intent itGps = new Intent(Settings.ACTION_SECURITY_SETTINGS);
					startActivity(itGps);
				}
			}

		};
		/*locationManager.requestLocationUpdates()
		 *
		 * 
		 * inicia o serviço de localização.OBS método recebe como parâmetros o
		 * tipo de provedor no caso GPS poderia ser por triangulação,ativando o
		 * INTERNET_PROVIDER,triangulação se não me engano o professor havia
		 * dito que usa torres não me lembro direito.
		 */
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10000, 0, locationListener);

	}

	
	
	public void Atualizar(Location location) {
		/*
		 * Atualizar()
		 * 
		 * esse método busca as coordenadas latitude e longitude e guarda o valor em suas respectivas
		 * váriaveis,esse método é utilizado dentro do método OnLocationChanged que é chamado quando a
		 * posição GPS muda
		 * 
		 * 
		 */
		
		
		Double latPoint = location.getLatitude();
		Double lngPoint = location.getLongitude();
		// Float velPoint=Location.getSpeed();//busca a velocidade não está sendo utilizado coloquei só por curiosidade
		String provPoint = location.getProvider();

		// txtVelocidade.setText(velPoint.toString());
		txtProvedor.setText(provPoint.toString());
		txtLatitude.setText(latPoint.toString());
		txtLongitude.setText(lngPoint.toString());

	}


	
	public void PararServico() {
		
		/*
	     * Método PararServiço
	 	 * 
	 	 * metodo para parar de receber coordenadas(estudar forma de implementar o
		 *locationManager.RemoveUpdates(),forma
		 *correta de fazer com que o aplicativo encerre o serviço de
		 *localização,(Estudar ciclo de Activities)
	 */

		
		finish();
//			mensagem informado que serviço de captura foi encerrado
		  Toast.makeText(AppOnibus.this, "Serviço desconectado",
		  Toast.LENGTH_LONG) .show(); finish();
		 
	}


	public void msg() {
		// msg toast de conectado, para utilizar comente o método ativa thread no btn
		// conectar
		Toast.makeText(this, "Conectado", Toast.LENGTH_LONG).show();

	}

	

	public void ativaThread() {
		// thread com progressdialog para carregar um dialogo enquanto dispositivo
		// localiza coord...(Estudar Threads e Handler)
		
		mDialog = ProgressDialog.show(this, "Buslocation",
				"Buscando coordenadas...", false, false);
		mHandler = new Handler();
		mThread = new MinhaThread(1);
		mThread.start();
	}

	
	
	public class MinhaThread extends Thread {
		private int numLoops;

		public MinhaThread(int loops) {
			numLoops = loops;
		}

		public void run() {
			Carregando();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mDialog.dismiss();
					Toast.makeText(AppOnibus.this,
							"Coordenadas Localizada pelo dispositivo",
							Toast.LENGTH_LONG).show();
				}
			});
		}

		private void Carregando() {
			int i = 0;
			do {
				try {
					Thread.sleep(30000);
					i++;
				} catch (InterruptedException e) {
				}
			} while (i < numLoops);
		}
	}

}
