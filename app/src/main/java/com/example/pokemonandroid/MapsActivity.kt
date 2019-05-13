package com.example.pokemonandroid

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermission()
        LoadPokemon()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.addMarker(MarkerOptions()
            .position(sydney)
            .title("Me")
            .snippet(" here is my location")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f))


    }
var ACCESSLOCATION=123
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
          if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
             requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
              return
          }
        }

        GetUserLocation()
    }

    fun GetUserLocation(){
        Toast.makeText(this, "User location access on", Toast.LENGTH_LONG).show()

      var mylocation= MylocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,mylocation)
       var myThread= myThread()
        myThread.start()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            ACCESSLOCATION->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    GetUserLocation()
                }
                else{
                    Toast.makeText(this,"We cannot access your location", Toast.LENGTH_LONG).show()
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    var location:Location?=null
    //Get user location

    inner class MylocationListener:LocationListener{


        constructor(){
            location = Location("Start")
            location!!.longitude=0.0
            location!!.longitude=0.0
        }
        override fun onLocationChanged(p0: Location?) {
                location=p0
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    var oldlocation:Location?=null
    inner class myThread:Thread{
        constructor():super(){
            oldlocation = Location("Start")
            oldlocation!!.longitude=0.0
            oldlocation!!.longitude=0.0
        }
        override fun run(){
            while(true){
                try{
                    if(oldlocation!!.distanceTo(location)==0f)
                    {
                        continue
                    }

                    oldlocation=location
                    runOnUiThread{
                        mMap!!.clear()

                        //show my location
                    val sydney = LatLng(location!!.latitude, location!!.longitude)
                    mMap!!.addMarker(MarkerOptions()
                        .position(sydney)
                        .title("Me")
                        .snippet(" here is my location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f))

                        //show pokemon

                        for(i in 0..listPokemon.size-1){
                            var newPokemon = listPokemon[i]
                            if(newPokemon.IsCatch==false){
                                val pokemonlocation = LatLng(newPokemon.location!!.latitude, newPokemon.location!!.longitude)
                                mMap!!.addMarker(MarkerOptions()
                                    .position(pokemonlocation)
                                    .title(newPokemon.name)
                                    .snippet(newPokemon.des +", power: "+ newPokemon!!.power)
                                    .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))

                                if(location!!.distanceTo(newPokemon.location)<2){
                                    newPokemon.IsCatch=true
                                    listPokemon[i]=newPokemon
                                    playerPower+=newPokemon.power!!
                                    Toast.makeText(applicationContext,"You catch the new pokemon, you new power is " + playerPower,
                                        Toast.LENGTH_LONG).show()
                                }

                            }
                        }
                }

                    Thread.sleep(1000)
                }
                catch(ex:Exception){}
            }
        }
    }

    var playerPower = 0.0
    var listPokemon=ArrayList<Pokemon>()

    fun LoadPokemon(){
        listPokemon.add(Pokemon(R.drawable.charmander,"Charmander","From Japan",55.0,37.77,-122.40))
        listPokemon.add(Pokemon(R.drawable.bulbasaur,"Bulbasaur","From USA",90.5,37.79,-122.41))
        listPokemon.add(Pokemon(R.drawable.squirtle,"Squirtle","From Iraq",33.5,37.78,-122.41))

    }
}
