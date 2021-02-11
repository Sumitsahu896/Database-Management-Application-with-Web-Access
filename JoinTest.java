package AppSlash.Tests;

import AppSlash.Code.*;

/**
 *Join vs indexed join tests
 */
public class JoinTest {
	/** Student Tables */
	private Table Student_1000, Student_2000, Student_5000, Student_10000, Student_50000;

	/** Professor Tables */
	private Table Professor_1000, Professor_2000, Professor_5000, Professor_10000, Professor_50000;

	/** Course Tables */
	private Table Course_1000, Course_2000, Course_5000, Course_10000, Course_50000;

	/** Teaching Tables */
	private Table Teaching_1000, Teaching_2000, Teaching_5000, Teaching_10000, Teaching_50000;

	/** Transcript Tables */
	private Table Transcript_1000, Transcript_2000, Transcript_5000, Transcript_10000, Transcript_50000;

	// used for timing
	private long begin;

	public static void main(String[] args)
	{
		JoinTest jt = new JoinTest();
		jt.Join("TreeMap");
		jt.Join("LinHashMap");
		jt.Join("BPTreeMap");
		jt.ijoin();
	}// main

	/*
	 * Get times for tables of length 1000, 2000, 5000, 10000, and 50000 for the specified data 
	 * structure - {"BPTreeMap", "LinHashMap", "TreeMap"}
	 * 
	 * @param ds the data structure to use for the index - {"BPTreeMap", "LinHashMap", "TreeMap"}
	 */
	public void Join(String ds)
	{
		this.setUp(ds);
		System.out.println(ds + " times:");

		this.begin = System.nanoTime();
		this.join1000();
		System.out.println("1000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		this.begin = System.nanoTime();
		this.join2000();
		System.out.println("2000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		this.begin = System.nanoTime();
		this.join5000();
		System.out.println("5000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		this.begin = System.nanoTime();
		this.join10000();
		System.out.println("10000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		this.begin = System.nanoTime();
		this.join50000();
		System.out.println("50000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		System.out.println();
	}// indexedTimes

	// needs to be run after this.setUp has already been called (any data structure)
	public void ijoin()
	{
		this.begin = System.nanoTime();
		this.ijoin1000();
		System.out.println("1000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		this.begin = System.nanoTime();
		this.ijoin2000();
		System.out.println("2000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		this.begin = System.nanoTime();
		this.ijoin5000();
		System.out.println("5000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		this.begin = System.nanoTime();
		this.ijoin10000();
		System.out.println("10000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");

		// this will take an incredibly long time
		this.begin = System.nanoTime();
		this.ijoin50000();
		System.out.println("50000: " + (System.nanoTime() - this.begin) / (double) 1000000000 + " s");
	}// nestedLoopTimes

	private void join1000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_1000.join("profId", "id", Professor_1000);
			Transcript_1000.join("studId",  "id",  Student_1000);
			Teaching_1000.join("crsCode",  "crsCode",  Course_1000);
		}
	}

	private void ijoin1000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_1000.i_join("profId", "id", Professor_1000);
			Transcript_1000.i_join("studId",  "id",  Student_1000);
			Teaching_1000.i_join("crsCode",  "crsCode",  Course_1000);
		}
	}

	private void join2000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_2000.join("profId", "id", Professor_2000);
			Transcript_2000.join("studId",  "id",  Student_2000);
			Teaching_2000.join("crsCode",  "crsCode",  Course_2000);
		}
	}

	private void ijoin2000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_2000. i_join("profId", "id", Professor_2000);
			Transcript_2000. i_join("studId",  "id",  Student_2000);
			Teaching_2000. i_join("crsCode",  "crsCode",  Course_2000);
		}
	}

	private void join5000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_5000.join("profId", "id", Professor_5000);
			Transcript_5000.join("studId",  "id",  Student_5000);
			Teaching_5000.join("crsCode",  "crsCode",  Course_5000);
		}
	}

	private void ijoin5000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_5000.i_join("profId", "id", Professor_5000);
			Transcript_5000.i_join("studId",  "id",  Student_5000);
			Teaching_5000.i_join("crsCode",  "crsCode",  Course_5000);
		}
	}

	private void join10000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_10000.join("profId", "id", Professor_10000);
			Transcript_10000.join("studId",  "id",  Student_10000);
			Teaching_10000.join("crsCode",  "crsCode",  Course_10000);
		}
	}

	private void ijoin10000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_10000.i_join("profId", "id", Professor_10000);
			Transcript_10000.i_join("studId",  "id",  Student_10000);
			Teaching_10000.i_join("crsCode",  "crsCode",  Course_10000);
		}
	}

	private void join50000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_50000.join("profId", "id", Professor_50000);
			Transcript_50000.join("studId",  "id",  Student_50000);
			Teaching_50000.join("crsCode",  "crsCode",  Course_50000);
		}
	}

	private void ijoin50000()
	{
		for(int i = 0; i < 10; i++)
		{
			Teaching_50000.i_join("profId", "id", Professor_50000);
			Transcript_50000.i_join("studId",  "id",  Student_50000);
			Teaching_50000.i_join("crsCode",  "crsCode",  Course_50000);
		}
	}

	/*
	 * Set up the Tables for the tests
	 * 
	 * @param ds the data structure to use for the index - {"BPTreeMap", "LinHashMap", "TreeMap"}
	 */
	@SuppressWarnings("rawtypes")
	private void setUp(String ds)
	{
		//Create Tables
		Student_1000 = new Table("Student", "id name address status", "Integer String String String", "id", ds);
		Student_2000 = new Table("Student", "id name address status", "Integer String String String", "id", ds);
		Student_5000 = new Table("Student", "id name address status", "Integer String String String", "id", ds);
		Student_10000 = new Table("Student", "id name address status", "Integer String String String", "id", ds);
		Student_50000 = new Table("Student", "id name address status", "Integer String String String", "id", ds);

		Professor_1000 = new Table("Professor", "id name deptId", "Integer String String", "id", ds);
		Professor_2000 = new Table("Professor", "id name deptId", "Integer String String", "id", ds);
		Professor_5000 = new Table("Professor", "id name deptId", "Integer String String", "id", ds);
		Professor_10000 = new Table("Professor", "id name deptId", "Integer String String", "id", ds);
		Professor_50000 = new Table("Professor", "id name deptId", "Integer String String", "id", ds);

		Course_1000 = new Table("Course", "crsCode deptId crsName descr", "String String String String", "crsCode", ds);
		Course_2000 = new Table("Course", "crsCode deptId crsName descr", "String String String String", "crsCode", ds);
		Course_5000 = new Table("Course", "crsCode deptId crsName descr", "String String String String", "crsCode", ds);
		Course_10000 = new Table("Course", "crsCode deptId crsName descr", "String String String String", "crsCode", ds);
		Course_50000 = new Table("Course", "crsCode deptId crsName descr", "String String String String", "crsCode", ds);

		Teaching_1000 = new Table("Teaching", "crsCode semester profId", "String String Integer", "crsCode semester", ds);
		Teaching_2000 = new Table("Teaching", "crsCode semester profId", "String String Integer", "crsCode semester", ds);
		Teaching_5000 = new Table("Teaching", "crsCode semester profId", "String String Integer", "crsCode semester", ds);
		Teaching_10000 = new Table("Teaching", "crsCode semester profId", "String String Integer", "crsCode semester", ds);
		Teaching_50000 = new Table("Teaching", "crsCode semester profId", "String String Integer", "crsCode semester", ds);

		Transcript_1000 = new Table("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester", ds);
		Transcript_2000 = new Table("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester", ds);
		Transcript_5000 = new Table("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester", ds);
		Transcript_10000 = new Table("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester", ds);
		Transcript_50000 = new Table("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester", ds);


		//Generate Data for those tuples
		TupleGenerator test = new TupleGeneratorImpl();

		//Schemas
		test.addRelSchema("Student", "id name address status", "Integer String String String", "id", null);
		test.addRelSchema("Professor", "id name deptId", "Integer String String", "id", null);
		test.addRelSchema("Course", "crsCode deptId crsName descr", "String String String String", "crsCode", null);
		test.addRelSchema("Teaching", "crsCode semester profId", "String String Integer", "crsCode semester", null);
		test.addRelSchema("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester", null);

		//Tuple sizes (all 50,000)
		int[] tups = new int[]{50000, 50000, 50000, 50000, 50000};

		//Generate random data
		Comparable[][][] resultTest = test.generate(tups);

		//Student Tables
		for (int i = 0; i < resultTest[0].length; i++) {
			if (i < 1000) {Student_1000.insert(resultTest[0][i]);}
			if (i < 2000) {Student_2000.insert(resultTest[0][i]);}
			if (i < 5000) {Student_5000.insert(resultTest[0][i]);}
			if (i < 10000) {Student_10000.insert(resultTest[0][i]);}
			Student_50000.insert(resultTest[0][i]);
		}
		//Professor Tables
		for (int i = 0; i < resultTest[1].length; i++) {
			if (i < 1000) {Professor_1000.insert(resultTest[1][i]);}
			if (i < 2000) {Professor_2000.insert(resultTest[1][i]);}
			if (i < 5000) {Professor_5000.insert(resultTest[1][i]);}
			if (i < 10000) {Professor_10000.insert(resultTest[1][i]);}
			Professor_50000.insert(resultTest[1][i]);
		}
		//Course Tables
		for (int i = 0; i < resultTest[2].length; i++) {
			if (i < 1000) {Course_1000.insert(resultTest[2][i]);}
			if (i < 2000) {Course_2000.insert(resultTest[2][i]);}
			if (i < 5000) {Course_5000.insert(resultTest[2][i]);}
			if (i < 10000) {Course_10000.insert(resultTest[2][i]);}
			Course_50000.insert(resultTest[2][i]);
		}
		//Teaching Tables
		for (int i = 0; i < resultTest[3].length; i++) {
			if (i < 1000) {Teaching_1000.insert(resultTest[3][i]);}
			if (i < 2000) {Teaching_2000.insert(resultTest[3][i]);}
			if (i < 5000) {Teaching_5000.insert(resultTest[3][i]);}
			if (i < 10000) {Teaching_10000.insert(resultTest[3][i]);}
			Teaching_50000.insert(resultTest[3][i]);
		}
		//Transcript Tables
		for (int i = 0; i < resultTest[4].length; i++) {
			if (i < 1000) {Transcript_1000.insert(resultTest[4][i]);}
			if (i < 2000) {Transcript_2000.insert(resultTest[4][i]);}
			if (i < 5000) {Transcript_5000.insert(resultTest[4][i]);}
			if (i < 10000) {Transcript_10000.insert(resultTest[4][i]);}
			Transcript_50000.insert(resultTest[4][i]);
		}
	}// setUp
}// JoinTest