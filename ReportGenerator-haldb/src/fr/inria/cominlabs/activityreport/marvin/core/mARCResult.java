package fr.inria.cominlabs.activityreport.marvin.core;



import java.lang.String;
import java.lang.String;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;




    public class mARCResult
    {
        public boolean _analyse;
        public ArrayList _columns;
        public ArrayList _data;
        public ArrayList _lines;
        public ArrayList _names;
        public ArrayList _sizes;
        public ArrayList _types;
        public boolean mError;
        public String mErrorMessage;
        public int mScriptSize;
        public int session_id;
        private String toReceive;

        public mARCResult()
        {
            this._lines = new ArrayList();
            this._columns = new ArrayList();
            this._data = new ArrayList();
            this._names = new ArrayList();
            this._types = new ArrayList();
            this._sizes = new ArrayList();
            this.session_id = -1;
        }

        public mARCResult(String ret)
        {
            this._lines = new ArrayList();
            this._columns = new ArrayList();
            this._data = new ArrayList();
            this._names = new ArrayList();
            this._types = new ArrayList();
            this._sizes = new ArrayList();
            this.Analyze(ret);
        }

        public void AnalyseLine(String line)
        {
            if ( (line!=null || !line.equals("") ) && (line.length() != 0))
            {
                ArrayList list = new ArrayList();
                ArrayList list2 = new ArrayList();
                ArrayList list3 = new ArrayList();
                Hashtable<String, ArrayList<String> > dictionary = new Hashtable();
                String[] strArray;
                int num3 = -1;
                int num4 = -1;
                while (line.startsWith(" ") || line.endsWith(" "))
                {
                    line = line.trim();
                }
                int index = line.indexOf('<');
                // attention au cas où une colonne contient "null" !!!!
                int indexNull = line.toLowerCase().indexOf("null");
                if (indexNull != -1)
                {

                    if (indexNull < index)
                        index = indexNull;
                }
                if (index == -1)
                {

                    strArray = line.split(" ");
                    num3 = (new Integer((strArray[0]))).intValue();
                    num4 = (new Integer((strArray[1]))).intValue();
                    this._lines.add(num3);
                    this._columns.add(num4);
                    this._types.add(list);
                    this._names.add(list2);
                    this._sizes.add(list3);
                    
                    if (num3 == 0 && num4 == 0)
                    {
                        this._data.add(dictionary);
                        this.mScriptSize++;
                        return;
                    }
                    strArray = line.split(" ");
                }
                else
                {
                    index--;
                    strArray = line.substring(0, index).split(" ");
                }

                if (num3 == -1 && num4 == -1)
                {
                    num3 = (new Integer((strArray[0]))).intValue();
                    num4 = (new Integer((strArray[1]))).intValue();
                    this._lines.add(num3);
                    this._columns.add(num4);
                    this._types.add(list);
                    this._names.add(list2);
                    this._sizes.add(list3);
                }

                int num5 = 2;
                // on récupère les infos de type et de noms des variables de la ligne courante
                for (int i = 0; i < num4; i++)
                {
                    int num7 = (new Integer((strArray[num5]))).intValue();
                    num5++;
                    list.add(mARC_connector.KMTypeLabel[num7]);
                    num7 = (new Integer((strArray[num5]))).intValue();
                    list3.add(num7);
                    num5++;
                    list2.add(strArray[num5]);
                    num5++;
                }

                if (index != -1)
                {
                    strArray = new KMString(line.substring(index)).split();

                    for (index = 0; index < list2.size(); index++)
                    {
                        ArrayList<String> list4 = new ArrayList();
                        for (int j = 0; j < num3; j++)
                        {
                            num5 = index + (j * num4);
                            list4.add(strArray[num5]);
                        }
                        dictionary.put((String)list2.get(index), list4);
                    }
                }

                this._data.add(dictionary);
                this.mScriptSize++;
            }
        }

        public void Analyze(String ret)
        {
            String[] strArray;
            int num2 = 0;
            this.mError = false;
            this.mErrorMessage = "";
            this.mScriptSize = 0;
            this.toReceive = ret;
            while (this.toReceive.startsWith(" ") || this.toReceive.endsWith(" "))
            {
                this.toReceive = this.toReceive.trim();
            }
            String toReceive = this.toReceive;
            // on élimine toutes les lignes 0 1 0 0  ; qui ne servent à rien



            // Console.WriteLine("Analyze toReceive = '" + this.toReceive + "'");

            int indexBegin = toReceive.indexOf('<');
            int pointvirguleIndex = toReceive.indexOf(';');
            int indexEnd = toReceive.indexOf("/>");

            if (((pointvirguleIndex == -1) || (indexBegin == -1)) || (indexEnd == -1))
            {
                strArray = new String[] { this.toReceive };
            }
            else
            {
                boolean flag = false;

                ArrayList<Integer> list = new ArrayList();
                int jj = 0, kk = 0;

                int current = 0;
            // on élimine toutes les lignes vides

                while (!flag)
                {
                    jj = toReceive.indexOf("0 1 0 0  ;", current);
                    kk = toReceive.indexOf("0 0  ;", current);

                    if ( jj != -1 && (pointvirguleIndex > 0 && jj > 0 && jj < pointvirguleIndex)) // on a une ligne qui ne sert à rien
                    {
                        list.add( jj+10 );
                        current = jj + 11;


                    }
                    else if (kk != -1 && (pointvirguleIndex > 0 && kk > 0 && kk < pointvirguleIndex))
                    {
                        list.add(kk + 6);
                        current = kk + 7;
                        
                    }
                    else 
                    {
                        // 1er cas  '<' ';' '/>'
                        if (indexBegin > 0 && indexEnd > 0 && pointvirguleIndex > 0 && (indexBegin < pointvirguleIndex && pointvirguleIndex < indexEnd))
                        {
                            // ';' est dans une chaine de caractères, on passe
                            current = indexEnd + 2;

                        }
                        // 2eme cas '/>' ';' '<'
                        else if (indexBegin > 0 && indexEnd > 0 && pointvirguleIndex > 0 && (indexEnd < pointvirguleIndex && pointvirguleIndex < indexBegin))
                        {
                            // ';' est dans une chaine de caractères, on passe
                            current = pointvirguleIndex + 1;
                            list.add(pointvirguleIndex);

                        }
                        // 3eme cas '<' '/>' ';' 
                        else if (indexBegin > 0 && indexEnd > 0 && pointvirguleIndex > 0 && (indexBegin < indexEnd && indexEnd < pointvirguleIndex ))
                        {
                            // ';' est il dans une chaine de caractères ?
                            int tmp1 = toReceive.indexOf('<', indexBegin + 1);
                            // on cherche le '<' le plus proche de ';'
                            jj = -1;
                            while (tmp1 != -1 && tmp1 < pointvirguleIndex)
                            {
                                jj = tmp1;
                                tmp1 = toReceive.indexOf('<', tmp1 + 1);
                            }
                            if (jj != -1)
                                tmp1 = jj;
                            int tmp2 = toReceive.indexOf("/>", pointvirguleIndex);
                            // 1er cas
                            // '<' ';' '/>'
                            if ( tmp2 != -1 && tmp1 != -1 && tmp1<tmp2) // oui on passe
                            {
                                current = tmp2 + 2;

                            }
                            else 
                            {    current = pointvirguleIndex + 1;
                                list.add(pointvirguleIndex);
                            }
                        }
                    }

                nextPattern:
              
                    pointvirguleIndex = toReceive.indexOf(';', current);
                    if (pointvirguleIndex == -1)
                    {
                        break;
                    }
                    indexBegin = toReceive.indexOf('<', current);
                    if (indexBegin == -1)
                    {
                        break;
                    }
                    indexEnd = toReceive.indexOf("/>", indexBegin);
                    if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
                    {
                        flag = true;
                    }
                }
                strArray = new String[list.size()];
                int num8 = 0;
                for (int i = 0; i < list.size(); i++)
                {
                    //Console.WriteLine("num8 " + num8 + " list[i] = " + (int) (list[i] - num8 + 1) );
                    strArray[i] = this.toReceive.substring(num8, (int) list.get(i) );
                    num8 = (int)  list.get(i) + 1;
                }
            }
            if (strArray.length == 0)
            {
                strArray = new String[1];
                strArray[0] = this.toReceive ;
            }

            String[] strArray2 = strArray[0].split(" ");
            this.session_id = (new Integer(strArray2[0])).intValue();
        	    
        	  //  Integer.getInteger(strArray2[0]).intValue();
            // Console.WriteLine("session id received from mARC server : " + this.session_id);
            num2 = (new Integer(strArray2[1])).intValue();
            this.mErrorMessage = "Ok";
            if (num2 == 0)
            {
                this.mErrorMessage = " error code : ";
                this.mErrorMessage = this.mErrorMessage + strArray2[2];
                this.mErrorMessage = this.mErrorMessage + " '";
                toReceive = "";
                for (int j = 3; j < strArray2.length; j++)
                {
                    toReceive = toReceive + strArray2[j] + " ";
                }
                strArray2 = new KMString(toReceive).split();
                if (strArray2 != null)
                {
                    this.mErrorMessage = this.mErrorMessage + strArray2[0];
                }
                this.mError = true;
            }
            else if (this._analyse)
            {
                if (strArray[0].indexOf('<') != -1)
                {
                    int num11 = strArray[0].indexOf(' ');
                    strArray[0] = strArray[0].substring(num11 + 1);
                    num11 = strArray[0].indexOf(' ');
                    strArray[0] = strArray[0].substring(num11 + 1);
                    // Console.WriteLine("on analyse la ligne 0 : '" + strArray[0] + "'");
                    this.AnalyseLine(strArray[0]);
                }
                else
                {
                    //ligne vide
                    this._lines.add(0);
                    this._columns.add(0);
                    ArrayList list = new ArrayList();
                    ArrayList list3 = new ArrayList();
                    ArrayList list2 = new ArrayList();
                    this._types.add(list);
                    this._names.add(list2);
                    this._sizes.add(list3);
                    Hashtable<String, ArrayList> dictionary2 = new Hashtable<String, ArrayList>();
                    this._data.add(dictionary2);
                    mScriptSize++;
                }
                for (int k = 1; k < strArray.length; k++)
                {
                    this.AnalyseLine(strArray[k]);
                }
            }
        }

        public void Clear()
        {
            this.session_id = -1;
            this._names.clear();
            this._columns.clear();
            this._lines.clear();
            this._data.clear();
            this._sizes.clear();
        }

        public void CopyFrom(mARCResult r)
        {
            this.session_id = r.session_id;
            this.mScriptSize = r.mScriptSize;
            this.mError = r.mError;
            this.mErrorMessage = r.mErrorMessage;
            for (int i = 0; i < r._lines.size(); i++)
            {
                this._lines.add(r._lines.get(i)  );
            }
            for (int j = 0; j < r._columns.size(); j++)
            {
                this._columns.add(r._columns.get(j));
            }
            for (int k = 0; k < r._sizes.size() ; k++)
            {
                this._sizes.add(r._sizes.get(k));
            }
            for (int m = 0; m < r._names.size(); m++)
            {
                ArrayList list = new ArrayList();
                ArrayList list2 = (ArrayList) r._names.get(m);
                for (int num5 = 0; num5 < list2.size(); num5++)
                {
                    list.add( list2.get(num5) );
                }
                this._names.add(list);
            }
            for (int n = 0; n < r._types.size(); n++)
            {
                ArrayList list3 = new ArrayList();
                ArrayList list4 = (ArrayList) r._types.get(n);
                for (int num7 = 0; num7 < list4.size(); num7++)
                {
                    list3.add(list4.get(num7) );
                }
                this._types.add(list3);
            }
            for (int num8 = 0; num8 < r._sizes.size(); num8++)
            {
                ArrayList list5 = new ArrayList();
                ArrayList list6 = (ArrayList) r._sizes.get(num8);
                for (int num9 = 0; num9 < list6.size(); num9++)
                {
                    list5.add(list6.get(num9));
                }
                this._sizes.add(list5);
            }
            for (int num10 = 0; num10 < r._data.size(); num10++)
            {
                Hashtable<String, ArrayList> dictionary = new Hashtable<String, ArrayList>();
                Hashtable<String, ArrayList> dictionary2 = (Hashtable<String, ArrayList>) r._data.get(num10);
                ArrayList<String> list7 = new ArrayList(dictionary2.keySet());
                for (String str : list7)
                {
                    ArrayList list8 = dictionary2.get(str);
                    ArrayList list9 = new ArrayList();
                    for (int num11 = 0; num11 < list8.size(); num11++)
                    {
                        list9.add((String) list8.get(num11) );
                    }
                    dictionary.put(str, list9);
                }
                this._data.add(dictionary);
            }
        }



        public String GetDataAt(int line, int col, int idx)
        {
            if (idx > (this.mScriptSize - 1))
            {
                return null;
            }
            if (idx == -1)
            {
                idx = this.mScriptSize - 1;
            }
            if (line >= ((Integer) this._lines.get(idx)) )
            {
                return null;
            }
            if (line < 0)
            {
                return null;
            }
            if (col > ((Integer) this._columns.get(idx) ))
            {
                return null;
            }
            if (col < 0)
            {
                return null;
            }
            Hashtable<String, ArrayList> dictionary = (Hashtable<String, ArrayList>) this._data.get(idx);
            ArrayList list = (ArrayList) this._names.get(idx);
            String str = (String) list.get(col);
            ArrayList list2 = dictionary.get(str);
            return (String) list2.get(line);
        }

        public String[] GetDataByName(String name, int idx)
        {
            if (idx == -1)
            {
                idx = this.mScriptSize - 1;
            }
            if (this._names.size() == 0)
            {
                return null;
            }
            ArrayList<String> list = (ArrayList) this._names.get(idx);
            if (!list.contains(name))
            {
                return null;
            }
            Hashtable<String, ArrayList> dictionary = (Hashtable<String, ArrayList>) this._data.get(idx);
            if (!dictionary.containsKey(name))
            {
                return null;
            }
            Object[] objArray = dictionary.get(name).toArray();
            String[] strArray = new String[objArray.length];
            int num = 0;
            for(Object obj2 : objArray)
            {
                strArray[num++] = (String) obj2;
            }
            return strArray;
        }

        public String[] GetDataByLine(int row, int idx)
        {
            if (idx == -1)
            {
                idx = this.mScriptSize - 1;
            }
            if (this._names.size() == 0)
            {
                return null;
            }

            int rows = (Integer) this._lines.get(idx) - 1;

            if ( row > rows || rows < 0 )
                return null;

            String[] result = new String[ (Integer) this._columns.get(idx) ];

            Hashtable<String, ArrayList> dictionary = (Hashtable<String, ArrayList>)this._data.get(idx);

            ArrayList<String> names = (ArrayList) this._names.get(idx); // les noms de la ligne de réponse idx

            for (int i = 0; i < names.size(); i++)
            {
                result[i] = (String) dictionary.get((String) names.get(i)).get(row);

                // on prend
            }
                return result;
        }

        public int rowsAtScriptLine(int idx)
        {
            if (idx > (this.mScriptSize - 1))
            {
                idx = this.mScriptSize - 1;
            }
            if (idx == -1)
            {
                idx = this.mScriptSize - 1;
            }
            return (Integer) this._lines.get(idx);
        }
    }


