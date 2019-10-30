package algorithms.catd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LinX
 */
public class CatdAlgorithm {

    private final List<Double> city_exp_pop; //entity?

    private final List<Double> user_weight; //source weight

    private final LoadData loadData;

    public CatdAlgorithm( final String input, final String yearF ) throws IOException {
        this.loadData = new LoadData( input, yearF );

        this.user_weight = Lists.newArrayList( Collections.nCopies( this.loadData.userId_userName.size(), 1.0 ) );
        this.city_exp_pop = Lists.newArrayList( Collections.nCopies( this.loadData.cityId_cityName.size(), 0.0 ) );
    }

    public static void main( final String[] args ) throws Exception {
        final CatdAlgorithm ab = new CatdAlgorithm( "getPirorTF", "yearFormat" );
        ab.run();
    }

    public void run() throws Exception {
        final int iter = 5;
        for (int i = 0; i < iter; i++) {
            update_user_weight();
            update_city_exp_pop();
        }

        final String fileOutput = "src/test/resources/algorithms/catd/result-Population-CATD.txt";
        final ImmutableList<Output> output = output();
        writeOutput( fileOutput, output );

        System.out.print( "-\t-\t" );
        getRMSE( output, o -> o.avg_to_num, "avg", this.loadData.GDScore );
        System.out.print( "-\t-\t" );
        getRMSE( output, o -> o.score, "CATD", this.loadData.GDScore );
    }

    public void getRMSE( final ImmutableList<Output> outputs, final Function<Output, Double> scoreProvider,
            final String note, final HashMap<Integer, Double> GDScore ) throws Exception {
        final AtomicDouble mae = new AtomicDouble( 0.0 );
        final AtomicDouble rmse = new AtomicDouble( 0.0 );
        final AtomicDouble error_rate = new AtomicDouble( 0 );
        outputs.forEach( output -> {
            final int id = output.city_id;
            if (GDScore.containsKey( id )) {
                final double score = scoreProvider.apply( output );
                final double errScore = Math.abs( GDScore.get( id ) - score );
                rmse.addAndGet( errScore * errScore );
                mae.addAndGet( errScore );
                if (errScore > GDScore.get( id ) * 0.01) {
                    error_rate.addAndGet( 1.0 );
                }
            }
        } );

        final int relevantOutput = Sets.intersection( GDScore.keySet(), outputs.stream().map( o -> o.city_id ).collect(
                Collectors.toSet() ) ).size();
        rmse.set( rmse.get() / relevantOutput );
        rmse.set( Math.sqrt( rmse.get() ) );
        error_rate.set( error_rate.get() / relevantOutput );
        mae.set( mae.get() / relevantOutput );
        System.out.println( note + "\tadd\t" + mae + "\t" + rmse + "\t" + error_rate + "\t" + relevantOutput );
    }

    public void update_user_weight() {
        double sum = 0.0;
        for (int user_id = 0; user_id < this.loadData.userId_userName.size(); user_id++) {
            double fenmu = 0.000001;
            final HashMap<Integer, Double> city_pop = this.loadData.user_city_pop.get( user_id );
            for (final int city_id : city_pop.keySet()) {
                final double tmp_movie_given_rate = city_pop.get( city_id );
                final double tmp_movie_exp_rate = this.city_exp_pop.get( city_id );
                fenmu += (tmp_movie_given_rate - tmp_movie_exp_rate) * (tmp_movie_given_rate - tmp_movie_exp_rate);
            }
            final double fenzi = new ChiSquaredDistribution( city_pop.size() ).inverseCumulativeProbability( 0.975 );
            final double tmp_score = fenzi / fenmu;
            this.user_weight.set( user_id, tmp_score );
            sum += tmp_score;
        }
        for (int user_id = 0; user_id < this.loadData.userId_userName.size(); user_id++) {
            final double weight = this.user_weight.get( user_id ) / sum;
            this.user_weight.set( user_id, weight );
        }
    }

    public void update_city_exp_pop() {
        for (int city_id = 0; city_id < this.loadData.cityId_cityName.size(); city_id++) {
            double fenzi = 0.0;
            double fenmu = 0.0;
            final HashMap<Integer, Double> user_rate = this.loadData.city_user_pop.get( city_id );
            for (final int user_id : user_rate.keySet()) {
                final double tmp_user_rate = user_rate.get( user_id );
                final double tmp_user_wt = this.user_weight.get( user_id );

                fenzi += tmp_user_rate * tmp_user_wt;
                fenmu += tmp_user_wt;
            }
            this.city_exp_pop.set( city_id, fenzi / fenmu );
        }
    }

    private ImmutableList<Output> output() throws Exception {
        return this.loadData.cityId_cityName.entrySet().stream().map(
                e -> new Output( e.getKey(), e.getValue(), this.loadData.getAverageToNum( e.getKey() ),
                        this.loadData.getScore( e.getKey(), this.city_exp_pop.get( e.getKey() ) ) ) ).collect(
                ImmutableList.toImmutableList() );
    }

    private void writeOutput( final String filename, final ImmutableList<Output> output ) throws IOException {
        final FileWriter writer = new FileWriter( filename );
        output.forEach( o -> {
            try {
                writer.write( o.city_id + "\t" + o.city_name + "\t" + o.avg_to_num + "\t" + o.score + "\n" );
            } catch (final IOException e) {
                throw new UncheckedIOException( e );
            }
        } );
        writer.close();
    }

    private class Output {
        private final int city_id;

        private final String city_name;

        private final double avg_to_num;

        private final double score;

        public Output( final int city_id, final String city_name, final double avg_to_num, final double score ) {
            this.city_id = city_id;
            this.city_name = city_name;
            this.avg_to_num = avg_to_num;
            this.score = score;
        }
    }
}
