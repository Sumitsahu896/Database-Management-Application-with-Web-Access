package AppSlash.Tests;

import AppSlash.Code.Table;
import org.junit.Test;

import static java.lang.System.out;
import static junit.framework.TestCase.assertTrue;

/****************************************************************************************
 * For this project JUnit 4 is used for test cases for the methods within Table.java
 * Tests are applied to the 5 classes within the project: Project, union, minus, natural join, and equi-join
 * (.select will be implemented in the following projects)
 */
public class TableTest {
    private static Table movie;
    private static Table cinema;
    private static Table movieStar;
    private static Table starsIn;
    private static Table movieExec;
    private static Table studio;

    private static void createTable() {
        out.println();

        movie = new Table("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        cinema = new Table("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        movieStar = new Table("movieStar", "name address gender birthdate",
                "String String Character String", "name");

        starsIn = new Table("starsIn", "movieTitle movieYear starName",
                "String Integer String", "movieTitle movieYear starName");

        movieExec = new Table("movieExec", "certNo name address fee",
                "Integer String String Float", "certNo");

        studio = new Table("studio", "name address presNo",
                "String String Integer", "name");

        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};
        var film2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125};
        var film3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355};

        out.println();
        movie.insert(film0);
        movie.insert(film1);
        movie.insert(film2);
        movie.insert(film3);
        movie.print();

        var film4 = new Comparable[]{"Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890};
        out.println();
        cinema.insert(film2);
        cinema.insert(film3);
        cinema.insert(film4);
        cinema.print();

        var star0 = new Comparable[]{"Carrie_Fisher", "Hollywood", 'F', "9/9/99"};
        var star1 = new Comparable[]{"Mark_Hamill", "Brentwood", 'M', "8/8/88"};
        var star2 = new Comparable[]{"Harrison_Ford", "Beverly_Hills", 'M', "7/7/77"};
        out.println();
        movieStar.insert(star0);
        movieStar.insert(star1);
        movieStar.insert(star2);
        movieStar.print();

        var cast0 = new Comparable[]{"Star_Wars", 1977, "Carrie_Fisher"};
        out.println();
        starsIn.insert(cast0);
        starsIn.print();

        out.println();
        out.println("Test Case I:  Adding Double data to Float");
        var exec0 = new Comparable[]{9999, "S_Spielberg", "Hollywood", 10000.00};
        out.println();
        movieExec.insert(exec0);
        movieExec.print();

        var studio0 = new Comparable[]{"Fox", "Los_Angeles", 7777};
        var studio1 = new Comparable[]{"Universal", "Universal_City", 8888};
        var studio2 = new Comparable[]{"DreamWorks", "Universal_City", 9999};
        out.println();
        studio.insert(studio0);
        studio.insert(studio1);
        studio.insert(studio2);
        studio.print();
    } //createTable


    /**
     * Project Method Testing
     */
    @Test
    public void project() {

        //Displaying the output(1) that is predicted for the .project query


        var projectOutput = new Table("projectOutput", "title year",
                "String Integer", "title year");

        var film0 = new Comparable[]{"Star_Wars", 1977};
        var film1 = new Comparable[]{"Star_Wars_2", 1980};
        var film2 = new Comparable[]{"Rocky", 1985};
        var film3 = new Comparable[]{"Rambo", 1978};

        out.println();
        projectOutput.insert(film0);
        projectOutput.insert(film1);
        projectOutput.insert(film2);
        projectOutput.insert(film3);
        projectOutput.print();

        //Test(1) for movie.project: (title year)
        out.println();
        out.println("Test Case I:  movie.project: title year");
        var t_project = movie.project("title year");
        assertTrue(projectOutput.equals(t_project));

        t_project.print();
        out.println("End of Test Case I:  movie.project: title year");
        out.println();



    } //project

    /**
     * Select method testing
     */
    @Test
    public void select() {

        createTable();

        //Displaying the output(1) that is predicted for the .select query

        var projectOutput = new Table("projectOutput", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        out.println();
        projectOutput.insert(film0);

        //Test(1) for movie.select: (equals, &&)

        out.println();
        out.println("Test Case I:  movie.select: equals, &&");
        var t_select = movie.select(t -> t[movie.col("title")].equals("Star_Wars") &&
                t[movie.col("year")].equals(1977));
        assertTrue(projectOutput.equals(t_select));
        t_select.print();
        out.println("End of Test Case I:  movie.select: equals, &&");
        out.println();


        //the output(3) that is predicted for the index select query

        var selectOutput1 = new Table("selectOutput1", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var film1 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        out.println();
        selectOutput1.insert(film1);

        //Test(3) for movie.select: (<)

        var outputMovie2 = new Table ("outputMovie2", "name address gender birthdate",
                "String String Character String", "name");

        var star0 = new Comparable [] { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
        out.println ();

        outputMovie2.insert (star0);

        //Test(3) for movie.select: (index)

        out.println("Test Case II: movie.select: <");
        var t_select2 = movie.select(t -> (Integer) t[movie.col("year")] < 1980);

        //assertTrue(outputMovie2.equals(t_select2));
        t_select2.print();
        out.println("End of Test Case II:  movie.select: <");


    } //select

    /**
     * Union method testing
     */
    @Test
    public void union() {

        //Displaying the output(1) that is predicted for the .union query


        var unionOutput = new Table("unionOutput", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};
        var film2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125};
        var film3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355};
        var film4 = new Comparable[]{"Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890};

        unionOutput.insert(film0);
        unionOutput.insert(film1);
        unionOutput.insert(film2);
        unionOutput.insert(film3);
        unionOutput.insert(film4);

        //Test(1) for movie.union: (cinema)
        // ---------------------Test Case I: union: movie UNION cinema---------------------------------------------------
        out.println();
        out.println("Test Case I: union: movie UNION cinema");
        var t_union = movie.union(cinema);
        assertTrue(unionOutput.equals(t_union));
        t_union.print();
        out.println("End of Test Case I: union: movie UNION cinema");
        out.println();


        //Displaying the output(1) - empty - that is predicted for the .union query

        var unionOutput1 = new Table("unionOutput", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

    } //union

    /**
     * Minus method testing
     */
    @Test
    public void minus() {

        //Displaying the output that is predicted for the movie minus cinema query


        var minusOutput = new Table("minusOutput", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};

        minusOutput.insert(film0);
        minusOutput.insert(film1);

        //Test(1) for movie.minus: (cinema)

        out.println();
        out.println("Test Case I: minus: movie MINUS cinema");
        var t_minus = movie.minus(cinema);
        assertTrue(minusOutput.equals(t_minus));
        t_minus.print();
        out.println("End of Test Case I: minus: movie MINUS cinema");
        out.println();


        //Displaying the output that is predicted for the cinema minus movie query


        var minusOutput1 = new Table("minusOutput1", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var film2 = new Comparable[]{"Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890};

        minusOutput1.insert(film2);

        //Test(2) for cinema.minus: (movie)
        out.println();
        out.println("Test Case II: minus: cinema MINUS movie");
        var t_minus1 = cinema.minus(movie);
        assertTrue(minusOutput1.equals(t_minus1));
        t_minus1.print();
        out.println("End of Test Case II: minus: cinema MINUS movie");
        out.println();


        //Displaying the output(3) that is predicted for the movie minus movie query


        var minusOutput2 = new Table("minusOutput2", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");


        //Test(3) for movie.minus: (movie)
        out.println();
        out.println("Test Case III: minus: movie MINUS movie");
        var t_minus2 = movie.minus(movie);
        assertTrue(minusOutput2.equals(t_minus2));
        t_minus2.print();
        out.println("End of Test Case III: minus: movie MINUS movie");
        out.println();


    } //minus

    /**
     * equi-join method testing
     */
    @Test
    public void equi_join() {

        //Creating the output that is predicted for the movie equi-join studio ON studioName=name query

        var joinOutput = new Table("joinOutput", "title year length genre studioName producerNo name " +
                "address presNo",
                "String Integer Integer String String Integer String String Integer", "title year");

        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345, "Fox", "Los_Angeles", 7777};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345, "Fox", "Los_Angeles", 7777};
        var film2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125, "Universal", "Universal_City",
                8888};
        var film3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355, "Universal", "Universal_City",
                8888};

        joinOutput.insert(film0);
        joinOutput.insert(film1);
        joinOutput.insert(film2);
        joinOutput.insert(film3);

        //Test(1) for movie - equi-join studio ON studioName = name
        out.println();
        out.println("Test Case I: equi-join: movie JOIN studio ON studioName = name");
        var t_join = movie.join("studioName", "name", studio);
        assertTrue(joinOutput.equals(t_join));
        t_join.print();
        out.println("End of Test Case I: equi-join: movie JOIN studio ON studioName = name");
        out.println();

        out.println();
        out.println("Test Case V: i-join: movie JOIN studio ON studioName = name");
        var i_join = movie.i_join("studioName", "name", studio);
        assertTrue(joinOutput.equals(i_join));
        i_join.print();
        out.println("End of Test Case V: i-join: movie JOIN studio ON studioName = name");
        out.println();

        //Displaying the output that is predicted for the movie equi-join cinema ON title = title query


        var joinOutput1 = new Table("joinOutput1", "title year length genre studioName producerNo " +
                "title2 year2 length2 genre2 studioName2 producerNo2",
                "String Integer Integer String String Integer String Integer Integer String String Integer",
                "title year");

        var film4 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125, "Rocky", 1985, 200,
                "action", "Universal", 12125};
        var film5 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355, "Rambo", 1978, 100,
                "action", "Universal", 32355};

        joinOutput1.insert(film4);
        joinOutput1.insert(film5);

        //Test(2) for movie - equi-join cinema ON title = title
        out.println();
        out.println("Test Case II: equi-join: movie JOIN cinema ON title = title");
        var t_join1 = movie.join("title", "title", cinema);
        assertTrue(joinOutput1.equals(t_join1));
        t_join1.print();
        out.println("End of Test Case II: equi-join: movie JOIN cinema ON title = title");
        out.println();


        //Displaying the output that is predicted for the movie equi-join starsIn ON title = movieTitle query


        var joinOutput2 = new Table("joinOutput2", "title year length genre studioName producerNo " +
                "movieTitle movieYear starName",
                "String Integer Integer String String Integer String Integer String", "title year");

        var film6 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345, "Star_Wars", 1977, "Carrie_Fisher"};

        joinOutput2.insert(film6);

        //Test(3) for movie - equi-join starsIn ON title = movieTitle

        out.println();
        out.println("Test Case III: equi-join: movie JOIN starsIn ON title = movieTitle");
        var t_join2 = movie.join("title", "movieTitle", starsIn);
        assertTrue(joinOutput2.equals(t_join2));
        t_join2.print();
        out.println("End of Test Case III: equi-join: movie JOIN starsIn ON title = movieTitle");
        out.println();
    } // equi-join
    /**
     * Test cases for natural join method
     */
    @Test
    public void natural_join() {
        //Creating the expected output table for the movie natural-join cinema

        var joinTable3 = new Table ("joinTable3", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year");

        var film7 = new Comparable [] { "Rocky", 1985, 200, "action", "Universal", 12125 };
        var film8 = new Comparable [] { "Rambo", 1978, 100, "action", "Universal", 32355 };

        joinTable3.insert(film7);
        joinTable3.insert(film8);

        //Test(1) natural join: movie JOIN cinema
        out.println ();
        out.println ("Test Case IV: natural join: movie JOIN cinema");
        var t_join3 = movie.join (cinema);
        assertTrue(joinTable3.equals(t_join3));
        t_join3.print ();
        out.println ("End of Test Case IV: natural join: movie JOIN cinema");
        out.println ();

    }//natural-join

}//class