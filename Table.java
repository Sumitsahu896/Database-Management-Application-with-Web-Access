package AppSlash.Code;
/****************************************************************************************
 * @file  ClassProject.Table.java
 *
 * @author   John Miller
 */

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.System.out;

/****************************************************************************************
 * This class implements relational database tables (including attribute names, domains
 * and a list of tuples.  Five basic relational algebra operators are provided: project,
 * select, union, minus and join.  The insert data manipulation operator is also provided.
 * Missing are update and delete data manipulation operators.
 */
public class Table
        implements Serializable
{
    /** Relative path for storage directory
     */
    private static final String DIR = "store" + File.separator;

    /** Filename extension for database files
     */
    private static final String EXT = ".dbf";

    /** Counter for naming temporary tables.
     */
    private static int count = 0;

    /** ClassProject.Table name.
     */
    private final String name;

    /** Array of attribute names.
     */
    private final String [] attribute;

    /** Array of attribute domains: a domain may be
     *  integer types: Long, Integer, Short, Byte
     *  real types: Double, Float
     *  string types: Character, String
     */
    private final Class [] domain;

    /** Collection of tuples (data storage).
     */
    private final List <Comparable []> tuples;

    /** Primary key.
     */
    private final String [] key;

    /** Index into tuples (maps key to tuple number).
     */
    private final Map <KeyType, Comparable []> index;

    /** The supported map types.
     */
    private enum MapType { NO_MAP, TREE_MAP, LINHASH_MAP, BPTREE_MAP }

    /** The map type to be used for indices.  Change as needed.
     */
    private static final MapType mType = MapType.BPTREE_MAP;

    /************************************************************************************
     * Make a map (index) given the MapType.
     */
    private static Map <KeyType, Comparable []> makeMap ()
    {
        if (mType == MapType.TREE_MAP) {
            return new TreeMap<>();
        } else if (mType == MapType.LINHASH_MAP) {
            return new LinHashMap<>(KeyType.class, Comparable[].class);
        } else if (mType == MapType.BPTREE_MAP) {
            return new BpTreeMap<>(KeyType.class, Comparable[].class);
        }
        return null;
    } // makeMap

    //-----------------------------------------------------------------------------------
    // Constructors
    //-----------------------------------------------------------------------------------

    /************************************************************************************
     * Construct an empty table from the meta-data specifications.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = new ArrayList <> ();
        index     = makeMap ();

    } // primary constructor

    /************************************************************************************
     * Constructs a table from the meta-data specifications and data in _tuples list.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     * @param _tuples     the list of tuples containing the data
     */

    public Table(String _name, String[] _attribute, Class[] _domain, String[] _key, List<Comparable[]> _tuples)
    {
        name = _name;
        attribute = _attribute;
        domain = _domain;
        key = _key;
        tuples = _tuples;
        index = makeMap();
    } // constructor



    /************************************************************************************
     * Constructs a table from the meta-data specifications, data in _tuples list and index in _index.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     * @param _tuples     the list of tuples containing the data
     * @param _index      the map containing existing index on the _key
     */


    public Table(String _name, String[] _attribute, Class[] _domain, String[] _key, List<Comparable[]> _tuples, Map<KeyType, Comparable[]> _index)
    {
        name = _name;
        attribute = _attribute;
        domain = _domain;
        key = _key;
        tuples = _tuples;
        index = _index;
    } // constructor

    /************************************************************************************
     * Construct an empty table from the raw string specifications.
     *
     * @param _name       the name of the relation
     * @param attributes  the string containing attributes names
     * @param domains     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table (String _name, String attributes, String domains, String _key)
    {
        this (_name, attributes.split (" "), findClass (domains.split (" ")), _key.split(" "));

        out.println ("DDL> create table " + name + " (" + attributes + ")");
    } // constructor

    //----------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Project the tuples onto a lower dimension by keeping only the given attributes.
     * Check whether the original key is included in the projection.
     *
     * #usage movie.project ("title year studioNo")
     *
     * @param attributes  the attributes to project onto
     * @return  a table of projected tuples
     */
    public Table project(String attributes) {
        out.println("RA> " + name + ".project (" + attributes + ")");

        var attrs = attributes.split(" ");
        var colDomain = extractDom(match(attrs), domain);
        var newKey = (Arrays.asList(attrs).containsAll(Arrays.asList(key))) ? key : attrs;

        List<Comparable[]> rows = new ArrayList<>();

        //  T O   B E   I M P L E M E N T E D
        Map<KeyType, Comparable[]> new_index = makeMap();
        try {
            rows = tuples.stream()
                    .map(t -> {
                        var proj_Tuple = Arrays.stream(attrs)
                                .map(column -> t[col(column)])
                                .collect(Collectors.toList()).toArray(new Comparable[attrs.length]);
                        if (new_index.get(new KeyType(proj_Tuple)) == null){
                            var keyValue = new Comparable[newKey.length];
                            var cols = match(newKey);
                            for (var j = 0; j < keyValue.length; j++) keyValue[j] = t[cols[j]];
                            if (mType != MapType.NO_MAP) new_index.put(new KeyType(keyValue), proj_Tuple);
                            return proj_Tuple;
                        }
                        return new Comparable[attrs.length];
                    })
                    .filter(t -> t[0] != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            out.println("Invalid Columns in projection. Please recheck the column name");
        }

        //rows = tuples.stream().map(eachTup -> extract(eachTup, attrs)).collect(Collectors.toList());
        return new Table(name + count++, attrs, colDomain, newKey, rows, new_index);
    } // project

    /************************************************************************************
     * Select the tuples satisfying the given predicate (Boolean function).
     *
     * #usage movie.select (t -> t[movie.col("year")].equals (1977))
     *
     * @param predicate  the check condition for tuples
     * @return  a table with tuples satisfying the predicate
     */
    public Table select (Predicate <Comparable []> predicate)
    {
        out.println ("RA> " + name + ".select (" + predicate + ")");

        return new Table (name + count++, attribute, domain, key,
                tuples.stream ().filter (t -> predicate.test (t))
                        .collect (Collectors.toList ()));
    } // select

    /************************************************************************************
     * Select the tuples satisfying the given key predicate (key = value).  Use an index
     * (Map) to retrieve the tuple with the given key value.
     *
     * @param keyVal  the given key value
     * @return  a table with the tuple satisfying the key predicate
     */

    public Table select(KeyType keyVal) {
        out.println("RA> " + name + ".select (" + keyVal + ")");

        List<Comparable[]> rows = new ArrayList<>();

        //  T O   B E   I M P L E M E N T E D
        Map<KeyType, Comparable[]> newindex = makeMap();
        var resRows = index.get(keyVal);
        if (resRows != null) {
            rows.add(resRows);
            var keyValue = new Comparable[key.length];
            var cols = match(key);
            for (var j = 0; j < keyValue.length; j++) keyValue[j] = resRows[cols[j]];
            if (mType != MapType.NO_MAP) newindex.put(new KeyType(keyValue), resRows);
        } else
            out.println("No row with " + keyVal.toString() + " found in the table");
        return new Table(name + count++, attribute, domain, key, rows, newindex);
    } // select

    /************************************************************************************
     * Union this table and table2.  Check that the two tables are compatible.
     *
     * #usage movie.union (show)
     *
     * @param table2  the rhs table in the union operation
     * @return  a table representing the union
     */

    public Table union(Table table2) {
        out.println("RA> " + name + ".union (" + table2.name + ")");
        if (!compatible(table2))
            return new Table(name + count++, attribute, domain, key, new ArrayList<>());

        //  T O   B E   I M P L E M E N T E D
        Map<KeyType, Comparable[]> newindex = makeMap();
        List<Comparable[]> rows = tuples.stream().map(resRow -> {
            var keyValue = new Comparable[key.length];
            var cols = match(key);
            for (var j = 0; j < keyValue.length; j++) keyValue[j] = resRow[cols[j]];
            if (mType != MapType.NO_MAP) newindex.put(new KeyType(keyValue), resRow);
            return resRow;
        }).collect(Collectors.toList());
        rows.addAll(table2.tuples.stream().filter(eachTup -> {
            var keyVal1 = new Comparable[table2.key.length];
            var cols = match(table2.key);
            for (var j = 0; j < keyVal1.length; j++) keyVal1[j] = eachTup[cols[j]];
            var existRow = newindex.get(new KeyType(keyVal1));
            newindex.putIfAbsent(new KeyType(keyVal1),eachTup);
            return existRow == null;
        }).collect(Collectors.toList()));
        return new Table(name + count++, attribute, domain, key, rows, newindex);
    } // union

    /************************************************************************************
     * Take the difference of this table and table2.  Check that the two tables are
     * compatible.
     *
     * #usage movie.minus (show)
     *
     * @param table2  The rhs table in the minus operation
     * @return  a table representing the difference
     */

    public Table minus(Table table2) {
        out.println("RA> " + name + ".minus (" + table2.name + ")");
        if (!compatible(table2)) return new Table(name + count++, attribute, domain, key, new ArrayList<>());

        List<Comparable[]> rows = new ArrayList<>();

        //  T O   B E   I M P L E M E N T E D
        Map<KeyType, Comparable[]> newindex = makeMap();
        if (Arrays.equals(this.key,table2.key)){
            rows = tuples.stream().filter(eachTup -> {
                var matched = false;
                var keyval = new Comparable[this.key.length];
                var cols = match(this.key);
                for (var j = 0; j < keyval.length; j++) keyval[j] = eachTup[cols[j]];
                var Tuple2 = table2.index.get(new KeyType(keyval));
                if (Tuple2 != null)
                    matched=true;
                if (!matched){
                    if (mType != MapType.NO_MAP) newindex.put(new KeyType(keyval), eachTup);
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        else{
            rows = tuples.stream().filter(eachTup -> {
                int n = table2.tuples.size();
                for (int i = 0; i < n; i++) if (!Arrays.equals(eachTup, table2.tuples.get(i))) return true;
                var keyValue = new Comparable[key.length];
                var cols = match(key);
                for (var j = 0; j < keyValue.length; j++) keyValue[j] = eachTup[cols[j]];
                if (mType != MapType.NO_MAP) newindex.put(new KeyType(keyValue), eachTup);
                return false;
            }).collect(Collectors.toList());
        }
        return new Table(name + count++, attribute, domain, key, rows, newindex);
    } // minus

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Tuples from both tables
     * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
     * names by append "2" to the end of any duplicate attribute name.  Implement using
     * a Nested Loop Join algorithm.
     *
     * #usage movie.join ("studioNo", "name", studio)
     *
     * @param attribute1  the attributes of this table to be compared (Foreign Key)
     * @param attribute2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */

    public Table join(String attribute1, String attribute2, Table table2) {
        out.println("RA> " + name + ".join (" + attribute1 + ", " + attribute2 + ", "
                + table2.name + ")");

        var t_attrs = attribute1.split(" ");
        var u_attrs = attribute2.split(" ");
        List<Comparable[]> rows = new ArrayList<Comparable[]>();

        //  T O   B E   I M P L E M E N T E D
        Map<KeyType, Comparable[]> newindex = makeMap();
        if (t_attrs.length != u_attrs.length) {
            out.println("Invalid join keys");
            return new Table(name + count++, attribute, domain, key, rows);
        }

        rows = tuples.stream().map(eachTup ->
        {
            for (int i = 0; i < table2.tuples.size(); i++)
            {
                Comparable[] Tuple2 = table2.tuples.get(i);
                if (Arrays.equals(extract(eachTup, t_attrs), extract(Tuple2, u_attrs)))
                {
                    return ArrayUtil.concat(eachTup, Tuple2);       //Add Index to RESULT
                }
            }
            return eachTup;
        }).filter(eachTup -> eachTup.length == attribute.length + table2.attribute.length).collect(Collectors.toList());


        //Processing duplicate attribute names
        var matchingAttributes = match(table2.attribute);
        String[] newAttrs = new String[table2.attribute.length];
        int cntrl = 0;
        for (int i = 0; i < matchingAttributes.length; i++)
        {
            if (matchingAttributes[i] == 0)
                cntrl++;
            if (cntrl > 1)
                break;
        }
        if (cntrl > 1)
            System.arraycopy(table2.attribute, 0, newAttrs, 0, matchingAttributes.length);
        else
            for (int i = 0; i < matchingAttributes.length; i++) newAttrs[i] = table2.attribute[matchingAttributes[i]] + "2";

        return new Table(name + count++, ArrayUtil.concat(attribute, newAttrs),
                ArrayUtil.concat(domain, table2.domain), key, rows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Same as above, but implemented
     * using an Index Join algorithm.
     *
     * @param attribute1  the attributes of this table to be compared (Foreign Key)
     * @param attribute2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table i_join(String attribute1, String attribute2, Table table2)
    {
        out.println("RA> " + name + ".index_join (" + attribute1 + ", " + attribute2 + ", "
                + table2.name + ")");

        var t_attrs = attribute1.split(" ");
        List<Comparable[]> rows;

        Map<KeyType, Comparable[]> newindex = makeMap();
        rows = tuples.stream().map(eachTup ->
        {
            var keyvalue = new Comparable[t_attrs.length];
            var cols = match(t_attrs);
            for (var j = 0; j < keyvalue.length; j++) keyvalue[j] = eachTup[cols[j]];
            var Tuple2 = table2.index.get(new KeyType(keyvalue));
            if (Tuple2 != null){
                if (mType != MapType.NO_MAP) newindex.put(new KeyType(keyvalue), eachTup);
                return ArrayUtil.concat(eachTup,Tuple2);
            }
            return eachTup;
        }).filter(eachTup -> eachTup.length != tuples.get(0).length).collect(Collectors.toList());

        //Processing duplicate attribute names
        var matchedAttributes = match(table2.attribute);
        String[] newAttributes = new String[table2.attribute.length];
        int cntrl = 0;
        for (int i = 0; i < matchedAttributes.length; i++) {
            if (matchedAttributes[i] == 0)
                cntrl++;
            if (cntrl > 1)
                break;
        }
        if (cntrl > 1)
            System.arraycopy(table2.attribute, 0, newAttributes, 0, matchedAttributes.length);
        else
            for (int i = 0; i < matchedAttributes.length; i++) newAttributes[i] = table2.attribute[matchedAttributes[i]] + "2";

        return new Table(name + count++, ArrayUtil.concat(attribute, newAttributes),
                ArrayUtil.concat(domain, table2.domain), key, rows, newindex);
    } // i_join


/*
    public Table i_join (String attributes1, String attributes2, Table table2)
    {
        return null;
    } // i_join
*/

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Same as above, but implemented
     * using a Hash Join algorithm.
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table h_join (String attributes1, String attributes2, Table table2)
    {
        return null;
    } // h_join

    /************************************************************************************
     * Join this table and table2 by performing an "natural join".  Tuples from both tables
     * are compared requiring common attributes to be equal.  The duplicate column is also
     * eliminated.
     *
     * #usage movieStar.join (starsIn)
     *
     * @param table2  the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */

    public Table join (Table table2)
    {
        out.println ("RA> " + name + ".join (" + table2.name + ")");
        List<Comparable[]> row;
        //  T O   B E   I M P L E M E N T E D
        List<String> common_attributes = new ArrayList<String>();
        //Finding the matched attributes from both the tables to determine the join key/keys.
        for (var j = 0; j < table2.attribute.length; j++)
        {
            for (var k = 0; k < attribute.length; k++)
            {
                if (table2.attribute[j].equals(attribute[k]))
                {
                    common_attributes.add(attribute[k]);
                }
            }
        }
        String[] c_attributes = common_attributes.toArray(new String[common_attributes.size()]);

        row = tuples.stream().map(eachTup ->
        {
            for (var Tup : table2.tuples)
                if (Arrays.equals(extract(eachTup, c_attributes), extract(Tup, c_attributes)))
                    return Arrays.stream(ArrayUtil.concat(eachTup, Tup)).distinct().collect(Collectors.toList())
                            .toArray(new Comparable[attribute.length + table2.attribute.length - c_attributes.length]);
            return new Comparable[0];
        }).filter(eachTup -> eachTup.length == attribute.length + table2.attribute.length - c_attributes.length).collect(Collectors.toList());

        var newAttributes = Arrays.stream(ArrayUtil.concat(attribute, table2.attribute)).distinct().collect(Collectors.toList())
                .toArray(new String[attribute.length + table2.attribute.length - c_attributes.length]);

        return new Table (name + count++, newAttributes, ArrayUtil.concat (domain, table2.domain), key, row);
    } // join
    /************************************************************************************
     * Return the column position for the given attribute name.
     *
     * @param attr  the given attribute name
     * @return  a column position
     */
    public int col (String attr)
    {
        for (var i = 0; i < attribute.length; i++) {
            if (attr.equals (attribute [i])) return i;
        } // for

        return -1;  // not found
    } // col

    /************************************************************************************
     * Insert a tuple to the table.
     *
     * #usage movie.insert ("'Star_Wars'", 1977, 124, "T", "Fox", 12345)
     *
     * @param tup  the array of attribute values forming the tuple
     * @return  whether insertion was successful
     */
    public boolean insert (Comparable [] tup)
    {
        out.println ("DML> insert into " + name + " values ( " + Arrays.toString (tup) + " )");

        if (typeCheck (tup)) {
            tuples.add (tup);
            var keyVal = new Comparable [key.length];
            var cols   = match (key);
            for (var j = 0; j < keyVal.length; j++) keyVal [j] = tup [cols [j]];
            if (mType != MapType.NO_MAP) index.put (new KeyType(keyVal), tup);
            return true;
        } else{
            return false;
        } // if
    } // insert

    /************************************************************************************
     * Get the name of the table.
     *
     * @return  the table's name
     */
    public String getName ()
    {
        return name;
    } // getName

    /************************************************************************************
     * Print this table.
     */
    public void print ()
    {
        out.println ("\n ClassProject.Table " + name);
        out.print ("|-");
        out.print ("---------------".repeat (attribute.length));
        out.println ("-|");
        out.print ("| ");
        for (var a : attribute) out.printf ("%15s", a);
        out.println (" |");
        out.print ("|-");
        out.print ("---------------".repeat (attribute.length));
        out.println ("-|");
        for (var tup : tuples) {
            out.print ("| ");
            for (var attr : tup) out.printf ("%15s", attr);
            out.println (" |");
        } // for
        out.print ("|-");
        out.print ("---------------".repeat (attribute.length));
        out.println ("-|");
    } // print

    /************************************************************************************
     * Print this table's index (Map).
     */
    public void printIndex ()
    {
        out.println ("\n Index for " + name);
        out.println ("-------------------");
        if (mType != MapType.NO_MAP) {
            for (var e : index.entrySet ()) {
                out.println (e.getKey () + " -> " + Arrays.toString (e.getValue ()));
            } // for
        } // if
        out.println ("-------------------");
    } // printIndex

    /************************************************************************************
     * Load the table with the given name into memory.
     *
     * @param name  the name of the table to load
     */
    public static Table load (String name)
    {
        Table tab = null;
        try {
            ObjectInputStream ois = new ObjectInputStream (new FileInputStream (DIR + name + EXT));
            tab = (Table) ois.readObject ();
            ois.close ();
        } catch (IOException ex) {
            out.println ("load: IO Exception");
            ex.printStackTrace ();
        } catch (ClassNotFoundException ex) {
            out.println ("load: Class Not Found Exception");
            ex.printStackTrace ();
        } // try
        return tab;
    } // load

    /************************************************************************************
     * Save this table in a file.
     */
    public void save ()
    {
        try {
            var oos = new ObjectOutputStream (new FileOutputStream (DIR + name + EXT));
            oos.writeObject (this);
            oos.close ();
        } catch (IOException ex) {
            out.println ("save: IO Exception");
            ex.printStackTrace ();
        } // try
    } // save

    //----------------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Determine whether the two tables (this and table2) are compatible, i.e., have
     * the same number of attributes each with the same corresponding domain.
     *
     * @param table2  the rhs table
     * @return  whether the two tables are compatible
     */
    private boolean compatible (Table table2)
    {
        if (domain.length != table2.domain.length) {
            out.println ("compatible ERROR: table have different arity");
            return false;
        } // if
        for (var j = 0; j < domain.length; j++) {
            if (domain [j] != table2.domain [j]) {
                out.println ("compatible ERROR: tables disagree on domain " + j);
                return false;
            } // if
        } // for
        return true;
    } // compatible

    /************************************************************************************
     * Match the column and attribute names to determine the domains.
     *
     * @param column  the array of column names
     * @return  an array of column index positions
     */
    private int [] match (String [] column)
    {
        int [] colPos = new int [column.length];

        for (var j = 0; j < column.length; j++) {
            var matched = false;
            for (var k = 0; k < attribute.length; k++) {
                if (column [j].equals (attribute [k])) {
                    matched = true;
                    colPos [j] = k;
                } // for
            } // for
            if ( ! matched) {
                //out.println ("match: domain not found for " + column [j]);
            } // if
        } // for
        return colPos;
    } // match

    /************************************************************************************
     * Extract the attributes specified by the column array from tuple t.
     *
     * @param t       the tuple to extract from
     * @param column  the array of column names
     * @return  a smaller tuple extracted from tuple t
     */
    private Comparable [] extract (Comparable [] t, String [] column)
    {
        var tup    = new Comparable [column.length];
        var colPos = match (column);
        for (var j = 0; j < column.length; j++) tup [j] = t [colPos [j]];
        return tup;
    } // extract

    /************************************************************************************
     * Check the size of the tuple (number of elements in list) as well as the type of
     * each value to ensure it is from the right domain.
     *
     * @param t  the tuple as a list of attribute values
     * @return  whether the tuple has the right size and values that comply
     *          with the given domains
     */
    private boolean typeCheck (Comparable [] t)
    {
        //  T O   B E   I M P L E M E N T E D

        //check if t is not empty and if the length is the same as the domain
        //Domain is used because it has the length of the table
        if(t.length != 0) {
            if (t.length != domain.length) {
                out.println ("typeCheck ERROR: t has different arity");
                return false;
            }//if
        }//if
        else{
            out.println ("typeCheck ERROR: t is Empty");
            return false;
        }

        //check if the t satisfies the domain constraints or has the same domains
        for(int i = 0; i < t.length; i++) {
            if(!t[i].getClass().getSimpleName().equals(domain[i].getSimpleName())) {
                out.println ("typeCheck ERROR: Domains dont match");
                return false;
            }//if
        }//for

        return true;
    } // typeCheck

    /************************************************************************************
     * Find the classes in the "java.lang" package with given names.
     *
     * @param className  the array of class name (e.g., {"Integer", "String"})
     * @return  an array of Java classes
     */
    private static Class [] findClass (String [] className)
    {
        var classArray = new Class [className.length];

        for (var i = 0; i < className.length; i++) {
            try {
                classArray [i] = Class.forName ("java.lang." + className [i]);
            } catch (ClassNotFoundException ex) {
                out.println ("findClass: " + ex);
            } // try
        } // for

        return classArray;
    } // findClass

    /************************************************************************************
     * Extract the corresponding domains.
     *
     * @param colPos the column positions to extract.
     * @param group  where to extract from
     * @return  the extracted domains
     */
    private Class [] extractDom (int [] colPos, Class [] group)
    {
        var obj = new Class [colPos.length];

        for (var j = 0; j < colPos.length; j++) {
            obj [j] = group [colPos [j]];
        } // for

        return obj;
    } // extractDom

    /************************************************************************************
     * Equals the two tables and retuns a boolean values
     *
     * @param table2  the comparing table
     * @return  a boolean value True if both table tuples are the same
     *                          False if both the table tuples has any difference
     */
    public Boolean equals (Table table2) {
        var flag = true;
        for(var i=0;i<tuples.size();i++){
            out.println(i);
            for(var j=0;j<tuples.get(i).length;j++){
                out.println(tuples.get(i)[j]);
                out.println(table2.tuples.get(i)[j]);
                if(! tuples.get(i)[j].equals(table2.tuples.get(i)[j])){
                    flag=false;
                }//if
            }//for
        }//for
        return flag;
    }// equals

    // returns number of tuples (for validation)
    public int size() {
        // return the one with higher value
        return Math.max(index.size(), tuples.size());
    }

}
