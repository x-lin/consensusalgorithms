package algorithms.catd;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LoadData {
    ArrayList<HashMap<Integer, Double>> city_user_pop;

    ArrayList<Double> city_avg_t;

    ArrayList<Double> city_prior;

    ArrayList<Double> city_sd;

    ArrayList<HashMap<Integer, Double>> user_city_pop;

    BiMap<Integer, String> cityId_cityName;

    BiMap<Integer, String> userId_userName;

    HashMap<Integer, Double> GDScore;

    public LoadData( final String input, final String yearF ) throws IOException {
        // init
        this.city_user_pop = new ArrayList<>();
        this.city_avg_t = new ArrayList<>();
        this.city_prior = new ArrayList<>();
        this.city_sd = new ArrayList<>();
        this.userId_userName = HashBiMap.create();
        this.user_city_pop = new ArrayList<>();

        // load
        String line = "";
        this.cityId_cityName = HashBiMap.create();
        int city_idx = 0;
        int user_idx = 0;
        final String add = "src/test/resources/algorithms/catd/population-new-new-new-TF.txt";
        final BufferedReader reader = new BufferedReader( new FileReader( new File( add ) ) );
        while ((line = reader.readLine()) != null) {
            final String[] tmp = line.split( "\t" );

            final String city_name = tmp[0];

            if (!this.cityId_cityName.inverse().containsKey( city_name )) {
                this.cityId_cityName.put( city_idx, city_name );
                this.city_user_pop.add( new HashMap<>() );
                this.city_avg_t.add( 0.0 );
                this.city_prior.add( 0.0 );
                this.city_sd.add( 0.0 );
                city_idx++;
            }
            if (!this.userId_userName.inverse().containsKey( tmp[1] )) {
                this.userId_userName.put( user_idx, tmp[1] );
                this.user_city_pop.add( new HashMap<>() );
                user_idx++;
            }

            final int city_index = this.cityId_cityName.inverse().get( city_name );
            final int user_index = this.userId_userName.inverse().get( tmp[1] );
            final double pop_t = Double.parseDouble( tmp[2] );

            if (input.equals( "getPirorTF" )) {
                final double prior = Double.parseDouble( tmp[3] );
                final double sd = Double.parseDouble( tmp[4] );
                this.city_prior.set( city_index, prior );
                this.city_sd.set( city_index, sd );
            }

            this.city_avg_t.set( city_index, this.city_avg_t.get( city_index ) + pop_t );
            this.user_city_pop.get( user_index ).put( city_index, pop_t );
            this.city_user_pop.get( city_index ).put( user_index, pop_t );
        }
        reader.close();

        // avg
        for (int city_id = 0; city_id < this.cityId_cityName.size(); city_id++) {
            final int count = this.city_user_pop.get( city_id ).size();
            this.city_avg_t.set( city_id, this.city_avg_t.get( city_id ) / count );
        }

        calculateGoldenScores( yearF );
    }

    double getScore( final int city_id, final double city_exp_pop ) {
        return city_exp_pop * this.city_sd.get( city_id ) + this.city_prior.get( city_id );
    }

    double getAverageToNum( final int city_id ) {
        return this.city_avg_t.get( city_id ) * this.city_sd.get( city_id ) + this.city_prior.get( city_id );
    }

    private void calculateGoldenScores( final String yearF ) throws IOException {
        final BufferedReader reader;
        String line;
        final HashSet<String> removeLines = new HashSet<>();
//		removeLines.add("chattanooga, tennessee, 2006, 168293");
//		removeLines.add("chattanooga, tennessee, 2000, 155554");
//		removeLines.add("milwaukee, wisconsin, 2000, 596974");
//		removeLines.add("milwaukee, wisconsin, 2006, 602782");
//		removeLines.add("st. louis, missouri, 2005, 352572");
//		removeLines.add("st. louis, missouri, 2006, 353837");
        removeLines.add( "st. joseph, missouri, 2000, 73990" );
        removeLines.add( "winston-salem, north carolina, 2000, 185776" );
        removeLines.add( "wilmington, delaware, 2000, 72664" );
        removeLines.add( "portland, oregon, 2000, 529121" );
        removeLines.add( "roswell, georgia, 2000, 79334" );
        removeLines.add( "minnetonka, minnesota, 2000, 51301" );
        removeLines.add( "lima, ohio, 2000, 40081" );
        removeLines.add( "vincennes, indiana, 2000, 18701" );
        removeLines.add( "douglasville, georgia, 2000, 20065" );
        removeLines.add( "nashua, new hampshire, 2000, 86605" );
        removeLines.add( "montague, michigan, 2000, 2407" );
        removeLines.add( "beverly, massachusetts, 2000, 39862" );
        removeLines.add( "albion, idaho, 2000, 262" );
        removeLines.add( "jessup, pennsylvania, 2000, 4718" );
        removeLines.add( "new berlin, wisconsin, 2000, 38220" );
        removeLines.add( "casper, wyoming, 2000, 49644" );
        removeLines.add( "kutztown, pennsylvania, 2000, 5067" );
        removeLines.add( "sweeny, texas, 2000, 3624" );
        removeLines.add( "john day, oregon, 2000, 1821" );
        removeLines.add( "winter park, colorado, 2000, 662" );
        removeLines.add( "fairfax, california, 2000, 7319" );
        removeLines.add( "rupert, idaho, 2000, 5645" );
        removeLines.add( "ettrick, wisconsin, 2000, 521" );
        removeLines.add( "heyburn, idaho, 2000, 2899" );
        removeLines.add( "minidoka, idaho, 2000, 129" );
        removeLines.add( "paul, idaho, 2000, 998" );
        removeLines.add( "bern, kansas, 2000, 204" );
        removeLines.add( "six mile, south carolina, 2000, 553" );
        removeLines.add( "poospatuck reservation, new york, 2000, 271" );
        removeLines.add( "ontario, california, 2000, 158007" );
        removeLines.add( "muskegon, michigan, 2000, 40105" );

        this.GDScore = new HashMap<>();
        reader = new BufferedReader(
                new FileReader( new File(
                        "src\\test\\resources\\algorithms\\catd\\population-gd.txt" ) ) );// testing data validation data
        while ((line = reader.readLine()) != null) {
            if (removeLines.contains( line )) {
                continue;
            }
            final String[] tmp = line.split( ", " );
            String city = tmp[0] + ", " + tmp[1];
            if (yearF.equals( "yearFormat" )) {
                city = tmp[0] + ", " + tmp[1] + "_" + tmp[2];
            }
            if (!this.cityId_cityName.inverse().containsKey( city )) {
                System.out.println( "-------------------" + line );
                continue;
            }

            final int id = this.cityId_cityName.inverse().get( city );
            this.GDScore.put( id, Double.parseDouble( tmp[3] ) );
        }
        System.out.println( this.GDScore.size() );
        reader.close();
    }

    public static void main( final String[] args ) {

    }

}
