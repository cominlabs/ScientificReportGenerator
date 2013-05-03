package fr.inria.cominlabs.activityreport.marvin.core;
import java.io.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class mARC_connector{

    public boolean _analyse; // si false on ne stocke pas les résultats
	// d�claration des propri�t�s
    public String IP;  //adresse IP defaut 127.0.0.1
    public int Port; // Port d�faut 1254
    public SocketChannel sock; // La socket cliente vers le serveur KM
    public PrintWriter out;
 	public BufferedReader in;
    public Boolean isConnected; //connexion km
    public Boolean isValid;      //socket IP valide
    public Boolean isError;
    public Boolean isBlocking;
    public String ErrorMsg;
    public int TimeLimit; //time out de connexion en s
    public String toSend ;	
    public String toReceive;
    public String Received;
    
    boolean _DirectExecute; // mode script ou execution ligne par ligne
    
    public int idx;

       
    //propri�t�s li�s � KM
    public  int[] KMSessions;	    	//tableau des id de connexion cr�es par l'objet
    public  String KMScriptSession;	//contient l'id de session pour le script
    public  mARCResult  _result;//  = new ArrayList<ArrayList<ArrayList<Object>>>();     

    
    public int KMError ;     // bool�en, retour d'erreur d'un appel de fonction KM
    public String KMErrorMsg ;  // bool�en, retour d'erreur d'un appel de fonction KM
    public String KMId; 		  //Id de session KM de l'objet KMServer
    public int KMCurrentId;  //Id de session KM de la derni�re requete
    
    public String KMFunction;   // fonction IKM � appeler
    public String KMParams;     // liste des parametres de la fonction KM    
    static public String[] KMTypeLabel = new String[15];     
    public KMString kmstring = new KMString();
    
    public   ArrayList<String> Params;  //Piles des commandes et params de script
    public   ArrayList<String> localParams = new ArrayList<String>(); 
    public String _name;
    
    public String ServerName;
    public String ServerBuild;
    
    public mARC_connector(String aName, String ip, int port)
    {
        _name = aName;
        this.IP=ip;
        this.Port=port;
        Initialize();
    }
    
    public mARC_connector()	 
	{   
            _name = "";
            Initialize();
        }
    public void Initialize()
    {

        _analyse = true;
        //IP = 			"88.189.240.38";
        KMError = 1;
       // IP = 			"127.0.0.1";
       // Port = 			1256;
        sock =			null;
        isConnected = 	false; // �tat de la connexion au serveur IKM
    	isValid = 		false; //la connection socket TCP est valide
   		isError = 		true;
    	isBlocking = 	true;
    	ErrorMsg = 		"TCP Socket not created";
    	TimeLimit =		10; 	//10 s de time out par d�faut, 0 pas de time out
    	KMId = 			"-1";		//pas d'Id de session IKM
    	toSend 	=		"";     //chaine � envoyer au serveur
    	toReceive =		"";     //chaine de retour du serveur
    	
    	_DirectExecute = true;
    	
    	_result=null;
    	//kmresultclasss=null;
    	System.gc();
    	_result  = new mARCResult();
    	//kmresultclasss = new ArrayList<KMResultsReader>();
    //	KMResults.add(new ArrayList<ArrayList<Object>>()); 
    	
    	//kmresultclasss.add(new KMResultsReader()); // Session 0
    	
    	

        KMScriptSession = "-1";
        KMSessions = new int[10];   		
       
    	KMTypeLabel [0] = "string"; 		KMTypeLabel [1] = "int32";		KMTypeLabel [2] = "uint32";
    	KMTypeLabel [3] = "int8"; 			KMTypeLabel [4] = "uint8";		KMTypeLabel [5] = "char";
    	KMTypeLabel [6] = "int64"; 			KMTypeLabel [7] = "uint64";		KMTypeLabel [8] = "string";
    	KMTypeLabel [9] = "float"; 			KMTypeLabel [10] = "double";	KMTypeLabel [11] = "bool";
    	KMTypeLabel [12] = "simpledate"; 	KMTypeLabel [13] = "rowid";		KMTypeLabel [14] = "sessionid";
    	System.gc();
       }    

    public String getReceived()
    {
        return Received;
    }

    public void finalize() 
	{
        //destruction des sessions KM cr�es par l'objet
        
		if (KMSessions!=null)
		{
		    for(int value : KMSessions ) 
		    {
    			CloseKMSession (value);
		    }	
		}
		
	//fin de la liaison physique	
		if (sock!=null) 
          {
          	 try {
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          	 sock = null;
          }
    }// end destruct 
    
    public boolean  ExecuteKMCommand (String str)
    { 
       //execute une commande d�j� format�e, un script par exemple
        
        if ( KMScriptSession.equals("-1") )
        Logger.getLogger("mARQTA").log(Level.INFO, "session ID is "+KMScriptSession+". could not execute command :: not connected ");
        
       // System.out.println("ExecuteKMCommand :: sessoin id "+KMScriptSession);
        
       toSend = KMScriptSession +" "+ str;
       
       return ExecuteScript();
    }
    
    public  boolean isNumeric(String str)  
    {  
      try  
      {          
		double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }     
    
    public boolean Execute(String ...params )
    { 
    	String session;
        toSend ="";
    	//params = func_get_args();
        //numargs = func_num_args();
        int idx = 0;
        String str =  params [idx];
        //1er parametre = id de session ?        
        if (isNumeric(str)) { session = str; idx +=1;}
        else { session = KMId;}
        
    	String function = params [idx];
    	idx +=1;
    	toSend = session;
    	toSend += ' ';
    	toSend  += function;
    	toSend  += " ( ";
    	for (int i=idx ; i < params.length; i++ ) 
    	{
    		str = params [i];    		    		
    		if ((str.compareToIgnoreCase("null")!=0 )&& (str.compareToIgnoreCase("default")!=0))
    		{
    			//kmstring.ToGPString();    
   //System.out.println("kmstring.ToGPString() aaaaaaaaaa");    			
    			kmstring.SetKMString(str);
    			kmstring.ToGPBinary();
    			str = kmstring.GetKMString();
 //   			System.out.println("str "+str);
    		}

    		toSend  += str;
    		toSend  += " " ;
    		if (i< params.length-1)
    		{
    			toSend += ", ";
    		}		
    		
    	}// end boucle $i
    	
    	toSend  += ')';    	
    	return ExecuteScript ();        
    }	
    
    public void OpenScript(String session)
    {
    	//utilise un num�ro de seeion, ou l'id de session par d�faut de l'objet connect� si pas d'argument
    	if (session == null) 	{session = KMId;} 
    	if ( Integer.parseInt(session) < 0) 		{session = KMId;} 
    	KMScriptSession = session;    	
    	toSend = KMScriptSession+" ";
    	_result.Clear();
    	localParams.clear();
    	
    }// 
    
    
    public boolean ExecuteScript()
    {
    	Send();
        Receive();
        Analyse();

        if (KMError == 0) return false;
    	return true ;
    	
    }// end KM_Execute_Script
 
    public void AddFunction()
    {
    	String[] params = new String[ localParams.size() ];
    	localParams.toArray( params );
    	AddFunction( params );
    }
    
    public void AddFunction(String ...params)
    {
    	//on va fabriquer la chaine � destination du serveur

    	String str;
    	toSend  += params[0]; 
    	toSend  += " ( ";
    	for (int i=1 ; i < params.length; i++ ) 
    	{
    		str = params [i];    		    		
    		if ((str.compareToIgnoreCase("null")!=0 )&& (str.compareToIgnoreCase("default")!=0))
    		{
			kmstring.SetKMString(str);
    			kmstring.ToGPBinary();
    			str = kmstring.str;
    		}

    		toSend  += str;
    		toSend  += ' ';
    		if (i< params.length-1)
    		{
    			toSend  += ", ";
    		}		
    		
    	}// end boucle $i
    	
    	toSend  += ") ;";
    	
    }// end AddFonction  

    public void CloseScript ()
    {
    	KMScriptSession = "-1";
    	if (toSend!=null) {toSend ="";}	
    	
    }// end KM_Close_Script       


	    
    public void Analyse() 
    {       
        //System.out.println("mARConnector Analyse Received '"+Received+"'");
    	if ( Received.isEmpty() )
    		return;
    	
    	_result.Clear();
        _result._analyse = _analyse;
    	_result.Analyze(Received);
        
        //System.out.println("mARConnector Analyse mError '"+_result.mError+"'");
        KMErrorMsg = "OK";
        KMError = 1;
    	if  ( _result.mError )
    	{
    		KMErrorMsg = _result.mErrorMessage;
    		KMError = 0;
    	}
	}// end analyse   
    
	
	public int OpenKMSession() 
	{
		//ouvre une nouvelle session KM
		//retourne -1 en cas d'erreur, un num�ro de session sinon
		//la liste des n� de session est mise � jour
		toSend = "-1 CONNECT (NULL);";
		ExecuteScript();
		if (KMError == 0) return -1;	
		KMSessions[KMSessions.length+1] = KMCurrentId  ;	
        return KMCurrentId;
		
	}// end OpenKMSession    

	public boolean CloseKMSession(int id) 
	{
		//d�truit la session $id. attention, ne jamais utiliser le num�ro de session de l'objet $KMId
		
		try {
			//Arrays.sort( KMSessions);
			int key = Arrays.binarySearch(KMSessions, id); // cela renvoie -11 c'est plutot etrange		
			if (key == 0) return false;		
			KMSessions[key]=0; //on vire la r�f�rence
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                // Execute (KMId,"SESSION.KILL",String.valueOf(id));
		return true;
		
	}//CloseKMSession   

	public boolean disConnect()
        {
            Logger.getLogger("mARQTA").log(Level.INFO, "DISconnecting from mARC server IP :"+IP+" Port : "+Port);
            if ( sock == null )
            {
                Logger.getLogger("mARQTA").log(Level.INFO, "socket was null so nothing was done.");
                isConnected = false;
                isValid = false;
                isError = false;
                KMScriptSession = "-1";
                return  true;
            }
            if ( sock.isOpen())
            {
                try
                {
                sock.close();
                isConnected = false;
                isValid = false;
                isError = false;
                KMScriptSession = "-1";
                Logger.getLogger("mARQTA").log(Level.INFO, "socket is closed. Disconnection is complete.");
                return true;
                }
                catch( Exception e)
                {
                    Logger.getLogger("mARQTA").log(Level.SEVERE, "ERROR : socket could NOT be closed.");
                    e.printStackTrace();
                    return false;
                }
            }
            KMScriptSession = "-1";
            isError = false;
            isConnected = false;
            isValid = false;
            return true;
        }
        public void clearResults()
        {
            _result.Clear();
        }
	public boolean Connect() 
	{
        if (isValid == true)
        {
        	//erreur, la connexion existe d�j�
        	ErrorMsg = 		"socket already exists : " ;
        	isError = 		true;
        	isValid = 		false;
        	return false;         	
        	
        }
		
		//on initialise le time out de connexion
		//set_time_limit($this->TimeLimit); // � voir comment �a se comporte le truc
		//set_time_limit(0); //pour debuggage
        //creation de la socket cliente, et connection au niveau IP
        //sur Linux, remplacer AF_INET, par AF_UNIX plus efficace
        //if ((Sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP)) === false)
        
        try 
        { 
            Logger.getLogger("mARQTA").log(Level.INFO, "connecting to mARC server IP :"+IP+" Port : "+Port);
        	sock = SocketChannel.open();
        	sock.configureBlocking(true);        	
        	sock.connect(new InetSocketAddress(IP,Port));
        	
        	//2. get Input and Output streams
            //out = new PrintWriter(sock.getOutputStream(),true);
            //in = new BufferedReader(new InputStreamReader( sock.getInputStream()));
			//3: Communicating with the server
        } 
        catch(Exception e) //on attrape l'erreur 
        { 
            Logger.getLogger("mARQTA").log(Level.SEVERE, "connection to mARC server REFUSED ");
        	ErrorMsg = "socket creation failure : " +e.getMessage().toString();
        	e.printStackTrace(); 
        	isError = 		true;
        	isValid = 		false;
        	return false; 
        }
            
        if (sock == null)
        {
            Logger.getLogger("mARQTA").log(Level.SEVERE, "connection to mARC server NOT VALID. Trying to close ");
        	try 
                {
                    sock.close();
                     Logger.getLogger("mARQTA").log(Level.INFO, "connection to mARC server closed. ");
		}
                catch (IOException e) 
                {
				// TODO Auto-generated catch block
				e.printStackTrace();
                 Logger.getLogger("mARQTA").log(Level.SEVERE, "connection to mARC server could not be closed. PANIC ");
                }        	
        	sock = null;
                isError = 		true;
                isValid = 		false;
        	return false;
        }
        
Logger.getLogger("mARQTA").log(Level.INFO, "connection to mARC server SUCCEEDED :: socket : "+sock.toString());
        //ici, la connection TCP a �t� �tablie
        	isValid = 		true;
        	isError = 		false;
        	ErrorMsg = 		"ok";
        	
        //maintenant on va tenter une connexion KM
        toSend = "-1 CONNECT (NULL);";
        Send();
//System.out.println("Send()");
        Receive();
//System.out.println("Receive();");
        Analyse();		
//System.out.println("Analyse();	");
        KMScriptSession = Integer.toString( _result.session_id );
        KMId = KMScriptSession;
        Logger.getLogger("mARQTA").log(Level.INFO, "session ID attributed by mARC server is '"+KMScriptSession+"'");
        // on détermine le nom du serveur auquel on vient de se connecter
        toSend = KMScriptSession+" Server.GetName();";
        Send();
        Receive();
        Analyse();
        String[] serverName = _result.GetDataByName("Name", -1);
        ServerName = serverName[0];
        Logger.getLogger("mARQTA").log(Level.INFO, "server name set to '"+ServerName+"'");
        // on détermine le build du serveur
        toSend = KMScriptSession+" Server.GetBuild();";
        
        Send();
        Receive();
        Analyse();
        System.out.println(toReceive);
        String[] buildName = _result.GetDataByName("Build", -1);
        ServerBuild = buildName[0];
        Logger.getLogger("mARQTA").log(Level.INFO, "server build set to '"+ServerBuild+"'");
        if (KMError == 0 ) return false;
        isConnected = true;
     //   KMId = String.valueOf(KMCurrentId) ;
		return true; 
		
    }// end connect
    
	
    public boolean Send() 
	{ 
		//traitement d'erreur à finir
		boolean ok = false;
		if (isValid == false) return ok;
		kmstring.SetKMString(toSend);
		kmstring.ToProtocol();
		toSend = kmstring.GetKMString();
                System.out.println("Send() msg toSend '"+toSend+"'");
		//Send data over socket
	      String text = toSend;
                           
	      ByteBuffer buf = ByteBuffer.allocate(text.length());
	      buf.clear();
              byte[] isobytes = text.getBytes(Charset.forName("ISO-8859-15"));
             // System.out.println("text.length() "+text.length());
             //for(int i=0; i<isobytes.length; i++)   {System.out.print((char)isobytes[i]);}
	      buf.put(SuppNoISO(isobytes, text.length()));
	      buf.flip();
//System.out.println("Start write" );
	      while(buf.hasRemaining()) {
	          try {
                    sock.write(buf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }    
//System.out.println("end write" );
	    //  out.println(text);	   
		ok = true;
		return ok;
	}//end function send	


    public  byte[] SuppNoISO(byte[]  isobytes, int lenghtbuf)
    {
         int j=0;
        byte[] bytetmp = new byte[lenghtbuf];
              //System.out.println("text.length() "+text.length());
             for(int i=0; i<isobytes.length; i++)   {
                 if(isobytes[i]<255)
                 {                     
                     bytetmp[j] = isobytes[i];
                     j++;
                 }
                // System.out.print((char)isobytes[i]);
             }
     return bytetmp;
     }


    public boolean Receive()
    {
       // System.out.println("start receive");
    	//Receive text from server
        Charset charset = Charset.forName("ISO-8859-1");
        CharsetDecoder decoder = charset.newDecoder();
       
     
    	Received="";
//    	System.out.println("Receive start ");
    	
    	String result = null;
    	Socket socket = null;
    	
          try{
	//traitement d'erreur à finir
		boolean ok = false;

		int ByteToReceive = 4096;
		int ByteReceived  = 0;
		boolean isHeaderOk = false;
		Received = "";

                 ByteBuffer buf = ByteBuffer.allocateDirect(ByteToReceive);
                 //ByteBuffer buf = ByteBuffer.allocate(ByteToReceive);

		while(ByteToReceive>0)
    		{
                    //System.out.println("start while receive "+ByteToReceive);
    			String recv = "";
    			ByteReceived+=sock.read(buf);           
    			buf.flip();
                        decoder.reset();
                        CharBuffer charBuf = decoder.decode(buf);
                        buf.clear();                    
                       recv+= charBuf.toString();
                       Received+=recv;
                      //  System.out.println("Received "+Received);
                       kmstring.SetKMString(Received);
                       ByteReceived+=recv.length();
                       ByteToReceive-=recv.length();

    			if (isHeaderOk == false)
    			{
    				if (Received.length()>2)
    				{
    					if (charBuf.get(0)!='#') {return false;}
    					int len1 = Integer.parseInt( Received.substring(1, 1+1));
    					if (len1 <= 0) {	return false;}
    					if (Received.length()> 5 + len1)
    					{
    						if (charBuf.get(2) != '#') 		{return false;}
    						if (charBuf.get(3+len1) != ' ') 	{return false;}
    						int len2 = Integer.parseInt( Received.substring(3, len1+3));                                               
                            Received = Received.substring( 4+len1);
                            kmstring.SetKMString(Received);
                                                
    						ByteReceived = Received.length();
    						ByteToReceive = len2-ByteReceived;
    						isHeaderOk = true;
    					}
    				}// end header
         //               System.out.println("start receive");
    			if (isHeaderOk == true)
            		{
                                  
//                            System.out.println("isHeaderOk == true");
                		if (ByteToReceive == 0) {
                                    // System.out.println("ByteToReceive == 0");
                                   return true;}
    			}
    		}
             }


          } catch (IOException e) {
 //           System.out.println("Read failed "+Received);
            
            System.exit(-1);
          }
          return false;

    }
    
    public static void print(ByteBuffer bb) {
        while (bb.hasRemaining())
//          System.out.print(bb.get() + " ");
//        System.out.println();
        bb.rewind();
      }

    // not in use
    public String UnicodetoIso(String _str)
    {
    	// Create the encoder and decoder for ISO-8859-1
    	Charset charset = Charset.forName("ISO-8859-1");
    	CharsetDecoder decoder = charset.newDecoder();
    	CharsetEncoder encoder = charset.newEncoder();

    	try {
    	    // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
    	    // The new ByteBuffer is ready to be read.
    	   ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(_str));

    	    // Convert ISO-LATIN-1 bytes in a ByteBuffer to a character ByteBuffer and then to a string.
    	    // The new ByteBuffer is ready to be read.
    	    CharBuffer cbuf = decoder.decode(bbuf);
    	    String s = cbuf.toString();
//    	    System.out.println("isotoUni s "+s);
    	    return s;
    	} catch (CharacterCodingException e) {
    		return e.toString();
    	}
    }
    
    public mARCResult GetMARCResult()
    {
    	return _result;
    }
    
 
    public void DoIt()
    {
       if ( _DirectExecute )
       {
           ExecuteScript();
       }
    }
    

    public void KnowledgeCreate(String name, String owner, String dir, String type, String filter)
    {
    	if ( _DirectExecute )     OpenScript(null);
    	localParams.clear();
    	Push ("KNOWLEDGE.Create");
    	Push(name);
    	Push(owner);
    	Push(dir);
    	Push(type);
    	Push(filter);
    	AddFunction();
        if ( _DirectExecute )     DoIt();
    }
    
    public void KnowLedgeSimilarDocs(String knwname, String dbID, String seuil, String maxSignature, String maxCount, String maxgen, String depth)
    {
    	if ( _DirectExecute )     OpenScript(null);
    	String s = "knowledge:"+knwname+"API_similarDoc";
    	Push(s);
    	Push(seuil);
    	Push(maxSignature);
    	Push(maxCount);
    	Push(maxgen);
    	Push(depth);
    	AddFunction();
    	if ( _DirectExecute ) DoIt();
    }
    
    public void KnowLedgeSimilarTitle(String knwname, String title, String seuil, String maxSignature, String maxCount)
    {
    	if ( _DirectExecute )     OpenScript(null);
    	String s = "knowledge:"+knwname+".API_similarTitle";
    	Push(s);
    	Push(title);
    	Push(seuil);
    	Push(maxSignature);
    	Push(maxCount);
    	AddFunction();
    	if ( _DirectExecute ) DoIt();
    }
    public void KnowLedgeSimilarContent(String knwname, String content, String seuil, String maxSignature, String maxCount)
    {
    	if ( _DirectExecute )     OpenScript(null);
    	String s = "knowledge:"+knwname+"API_similarContent";
    	Push(s);
    	Push(content);
    	Push(seuil);
    	Push(maxSignature);
    	Push(maxCount);
    	AddFunction();
    	if ( _DirectExecute ) DoIt();
    }
    
    
    
   /* public void KnowledgeKill(String name)
    {
    	if ( _DirectExecute )     OpenScript(null);
    	localParams.clear();
    	Push ("KNOWLEDGE.Kill");
    	Push(name);
    	AddFunction();
        if ( _DirectExecute )     DoIt();
    }
    public void KnowledgeRename(String name, String newname)
    {
    	if ( _DirectExecute )     OpenScript(null);
    	localParams.clear();
    	Push ("KNOWLEDGE.Rename");
    	Push(name);
    	Push(newname);
    	AddFunction();
        if ( _DirectExecute )     DoIt();
    }
    //________________________________________________
    public void KnowledgeGetInstances ()
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       Push ("KNOWLEDGE.GET");
       Push ("INSTANCES");
       AddFunction();
       if ( _DirectExecute )     DoIt();


    } */
    //________________________________________________
    public void KnowledgeSave(String knw)
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       String tmp = "KNOWLEDGE:"+knw+".Save";
       Push (tmp);
       AddFunction();
       if ( _DirectExecute )     DoIt();


    }
  /*  public void KnowledgeReload(String knw)
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       String tmp = "KNOWLEDGE:"+knw+".Reload";
       Push (tmp);
       AddFunction();
       if ( _DirectExecute )     DoIt();
    }    
    public void KnowledgeShrink(String knw)
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       String tmp = "KNOWLEDGE:"+knw+".Shrink";
       Push (tmp);
       AddFunction();
       if ( _DirectExecute )     DoIt();
    }    
    public void KnowledgeKillRef(String knw, int threshold)
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       String tmp = "KNOWLEDGE:"+knw+".KillRef";
       Push (tmp);
       Push (Integer.toString(threshold));
       AddFunction();
       if ( _DirectExecute )     DoIt();
    } 
    public void KnowledgeKillSem(String knw, int threshold)
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       String tmp = "KNOWLEDGE:"+knw+".KillSem";
       Push (tmp);
       Push (Integer.toString(threshold));
       AddFunction();
       if ( _DirectExecute )     DoIt();
    }
    public void KnowledgeClear(String knw)
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       String tmp = "KNOWLEDGE:"+knw+".Clear";
       Push (tmp);
       AddFunction();
       if ( _DirectExecute )     DoIt();
    }
*/
    
    public void KnowledgeRebuild(String knw, String column, int db_id_begin, int db_id_end, String options)
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       String tmp = "KNOWLEDGE:"+knw+".Rebuild";
       Push (tmp);
       Push( column);
       Push (Integer.toString(db_id_begin));
       Push (Integer.toString(db_id_end));
       Push( options );
       AddFunction();
       if ( _DirectExecute )     DoIt();
    } 
    public void KnowledgePublish(String knw)
    {
       if ( _DirectExecute )     OpenScript(null);
       
       localParams.clear();
       String tmp = "KNOWLEDGE:"+knw+".Publish";
       Push (tmp);
       AddFunction();
       if ( _DirectExecute )     DoIt();
    }
    //________________________________________________
  /*  public void KnowledgeProperties (String knw)
    {
       String tmp;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Knowledge:";
       tmp += knw;
       tmp += ".properties";
       Push (tmp);
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void KnowledgeTasks (String knw)
    {
       String tmp;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Knowledge:";
       tmp += knw;
       tmp += ".Tasks";
       Push (tmp);
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }*/
    
    public void KnowledgeLearn (String knw, String textToLearn, String row_id, String seuil, String actmin, String actmax)
    {
       String tmp;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Knowledge:";
       tmp += knw;
       tmp += ".Learn";
       Push (tmp);
       Push (textToLearn);
       Push( row_id);
       Push( seuil);
       Push( actmin);
       Push( actmax);
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    
  /*  public void KnowledgeSetIndexationTimeout(String knw, int time_ms)
    {
        String tmp;
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        tmp = "Knowledge:";
        tmp += knw;
        tmp += ".SetIndexationTimeout";
        Push (tmp);
        Push (Integer.toString(time_ms));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();
         }
    }
    public void KnowledgeSetMaxCels(String knw, int maxcels)
    {
        String tmp;
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        tmp = "Knowledge:";
        tmp += knw;
        tmp += ".SetMaxCels";
        Push (tmp);
        Push (Integer.toString(maxcels));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();
         }
    }
    public void KnowledgeSetMaxLinks(String knw, int maxlinks)
    {
        String tmp;
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        tmp = "Knowledge:";
        tmp += knw;
        tmp += ".SetMaxLinks";
        Push (tmp);
        Push (Integer.toString(maxlinks));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();
         }
    }
    public void KnowledgeGetIndexationCache(String knw)
    {
        String tmp;
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        tmp = "Knowledge:";
        tmp += knw;
        tmp += ".GetIndexationCache";
        Push (tmp);
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();
         }
    } 
    public void KnowledgeGetIndexationCacheUsed(String knw)
    {
        String tmp;
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        tmp = "Knowledge:";
        tmp += knw;
        tmp += ".GetIndexationCacheUsed";
        Push (tmp);
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();
         }
    }
    public void KnowledgeGetIndexationTimeOut(String knw)
    {
        String tmp;
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        tmp = "Knowledge:";
        tmp += knw;
        tmp += ".GetIndexationTimeOut";
        Push (tmp);
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();
         }
    }
*/

    //________________________________________________
    public double GetLastTime ()
    {

       if (_DirectExecute)           
    	   OpenScript(null);
       localParams.clear();
       Push ("GetLastTime");
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
           return Integer.getInteger( _result.GetDataAt(0,0,-1) );
        }
            
    	   return -1.;
    }

  /*  //________________________________________________
    public void ContextsNew ()
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push("Contexts.New");
       AddFunction ();
       if (_DirectExecute)     DoIt();

    }
    //________________________________________________
    public void ContextsClear ()
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.Clear");
       AddFunction ();
       if (_DirectExecute)      DoIt();
    }
    //________________________________________________
    public void ContextsSetKnowledge(String s)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.Set");
       Push ("Knowledge");
       Push (s);
       AddFunction ();
       if (_DirectExecute)      DoIt();

    }
    //________________________________________________
    public void ContextsAddAtom (String s)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.AddAtom");
       Push (s);
       AddFunction();
       if (_DirectExecute)      DoIt();

    }

    public void ContextsAddCelIds (ArrayList<String> s)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.AddCelIds");
       for (int i = 0; i < s.size();i++)
       {
    	   Push(s.get(i));
       }
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    
    public void ContextsAddFromTable (String name, int column, int dbid)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.AddFromTable");
       
    	   Push(name);
    	   Push( Integer.toString(column));
    	   Push( Integer.toString(dbid) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }    

    public void ContextsAddText (String text, String script)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.AddText");
       Push(text);
       Push( script );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    */
    
    public void ContextsSetKnowledge(String knw)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Contexts.Set");
        Push( "Knowledge" );
        Push( knw );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    
    public void KnowledgeAPI_ContextsGetBestResults(String knw, String query)
    {
	       if (_DirectExecute)     OpenScript(null);
	       localParams.clear();
	       Push ("Knowledge:"+knw+".API_GetBestResults");
	       Push(query);
	       AddFunction();
	       if (_DirectExecute)      DoIt();
    }
    
    public void KnowLedgeAPI_ContextsGetBestWords(String knw, String query, String maxShape, String maxRelated)
    {
	       if (_DirectExecute)     OpenScript(null);
	       localParams.clear();
	       Push ("Knowledge:"+knw+".API_GetBestWords");
	       Push(query);
	       Push(maxShape);
	       Push(maxRelated);
	       /*
	       Push( String.valueOf(celsId.length ) );
	       for (int i = 0; i < celsId.length;i++)
	       {
		   Push( celsId[i]);
	       }
	       */
	       AddFunction();
	       if (_DirectExecute)      DoIt();
    }
    
    
    public void ContextsGetBestResults(String query)
    {
	       if (_DirectExecute)     OpenScript(null);
	       localParams.clear();
	       Push ("Contexts.GetBestResults");
	       Push(query);
	       AddFunction();
	       if (_DirectExecute)      DoIt();
    }
    
    public void ContextsGetBestWords(String query, String maxShape, String maxRelated, String celsId[] )
    {
	       if (_DirectExecute)     OpenScript(null);
	       localParams.clear();
	       Push ("Contexts.GetBestWords");
	       Push(query);
	       Push(maxShape);
	       Push(maxRelated);
	       Push( String.valueOf(celsId.length ) );
	       for (int i = 0; i < celsId.length;i++)
	       {
		   Push( celsId[i]);
	       }
	       AddFunction();
	       if (_DirectExecute)      DoIt();
    }
   /* public void ContextsAmplify ()
    {
    	ContextsAmplify( -1. , Double.MIN_VALUE);
    }
    
    public void ContextsAmplify (double multiplier, double max)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.AddText");
       Push(Double.toString(multiplier));
       Push( Double.toString(max) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }  
    
    public void ContextsCelActivate()
    {
    	ContextsCelActivate(true,false);
    }
    public void ContextsCelActivate (boolean activate, boolean global)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.CelActivate");
       Push( Boolean.toString(activate) );
       Push( Boolean.toString(global) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    public void ContextsCelBoost()
    {
    	ContextsCelBoost(true,true,false);
    }
    public void ContextsCelBoost (boolean boost, boolean global, boolean incremental)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.CelBoost");
       Push( Boolean.toString(boost) );
       Push( Boolean.toString(global) );
       Push( Boolean.toString(incremental) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    
    public void ContextsCelInhibit()
    {
    	ContextsCelInhibit(true,false);
    }
    
    public void ContextsCelInhibit (boolean inhibit, boolean global )
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.CelInhibit");
       Push( Boolean.toString(inhibit) );
       Push( Boolean.toString(global) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }   
    public void ContextsCelSelect()
    {
    	ContextsCelSelect(true,false);
    }
    public void ContextsCelSelect (boolean select, boolean global )
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.CelSelect");
       Push( Boolean.toString(select) );
       Push( Boolean.toString(global) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    
    public void ContextsCelTrace()
    {
    	ContextsCelTrace(true,false);
    }
    public void ContextsCelTrace (boolean trace, boolean global )
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.CelTrace");
       Push( Boolean.toString(trace) );
       Push( Boolean.toString(global) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    
    public void ContextsDeleteAtom()
    {
    	ContextsDeleteAtom(0);
    }
    
    public void ContextsDeleteAtom (int index )
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.DeleteAtom");
       Push( Integer.toString(index) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    public void ContextsDiffuseContext ()
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.DiffuseContext");
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    
    public void ContextsDrop ()
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.Drop");
       AddFunction();
       if (_DirectExecute)      DoIt();
    }

    public void ContextsDup ()
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.Dup");
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    public void ContextsEvaluate (String evaluate)
    {
    	if (evaluate == "" || evaluate == null )
    		evaluate ="contextual";
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.Evaluate");
       Push( evaluate );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    public void ContextsFilterAct ()
    {
    	ContextsFilterAct (Integer.MIN_VALUE, true );
    }
    
    public void ContextsFilterAct (int minimum, boolean more_than )
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.FilterAct");
       Push( Integer.toString(minimum) );
       Push(Boolean.toString( more_than ) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    
    public void ContextsFilterActivated()
    {
    	ContextsFilterActivated(true);
    }
    
    public void ContextsFilterActivated (boolean ok )
    {                        

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.FilterActivated");
       Push(Boolean.toString( ok ) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    
    public void ContextsFilterComposite()
    {
    	ContextsFilterComposite(true);
    }
    
    public void ContextsFilterComposite(boolean ok )
    {                        

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.FilterComposite");
       Push(Boolean.toString( ok ) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }

    public void ContextsFilterGen ()
    {
    	ContextsFilterGen (1 , true );
    }
    
    public void ContextsFilterGen (int minimum, boolean more_than )
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.FilterGen");
       Push( Integer.toString(minimum) );
       Push(Boolean.toString( more_than ) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }

    public void ContextsFilterInhibited()
    {
    	ContextsFilterInhibited(true);
    }
    
    public void ContextsFilterInhibited(boolean ok )
    {                        

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.FilterInhibited");
       Push(Boolean.toString( ok ) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }

    public void ContextsFilterSegment()
    {
    	ContextsFilterSegment(true);
    }
    public void ContextsFilterSegment(boolean meaningless )
    {                        

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.FilterSegment");
       Push(Boolean.toString( meaningless ) );
       AddFunction();
       if (_DirectExecute)      DoIt();
    }
    public void ContextsFilterSelected()
    {
    	ContextsFilterSelected(true);
    }
    public void ContextsFilterSelected(boolean ok )
    {                        

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.FilterSelected");
       Push(Boolean.toString( ok ) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    public void ContextsFilterTraced()
    {
    	ContextsFilterTraced(true);
    }
    public void ContextsFilterTraced(boolean ok )
    {                        

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.FilterTraced");
       Push(Boolean.toString( ok ) );
       AddFunction();
       if (_DirectExecute)      DoIt();

    }
    
    //________________________________________________
    public void ContextsGetStack (int max_context, int max_context_size)
    {

       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Contexts.GetStack");
       Push (Integer.toString(max_context));
       Push (Integer.toString(max_context_size));
       AddFunction ();
       if (_DirectExecute)              DoIt();

    }
    
    public void ContextsGetAtoms ()
    {
    	ContextsGetAtoms (-1, 0);
    }
    public void ContextsGetAtoms (int max_count, int start)
    {

       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Contexts.GetStack");
       Push (Integer.toString(max_count));
       Push (Integer.toString(start));
       AddFunction ();
       if (_DirectExecute)              DoIt();

    }   
    public void ContextsGetBestWords(int context_count, ArrayList<Integer> celids )
    {
    	ContextsGetBestWords(-1,-1,context_count,celids);
    }
    public void ContextsGetBestWords(int max_shapes, int max_related, int context_count, ArrayList<Integer> celids )
    {

       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Contexts.GetBestWords");
       Push (Integer.toString(max_shapes));
       Push (Integer.toString(max_related));
       Push (Integer.toString(context_count));
       for (int i = 0; i < celids.size() ; i++)
       {
    	   Push (Integer.toString(celids.get(i)));
       }
       AddFunction ();
       if (_DirectExecute)              DoIt();

    }    

    public void ContextsIndex( String table)
    {
    	ContextsIndex(table, -1 );
    }
    public void ContextsIndex(String table, int dbid )
    {

       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Contexts.Index");
       Push ( table );
       Push (Integer.toString(dbid));
       AddFunction ();
       if (_DirectExecute)              DoIt();

    }     

    public void ContextsIntersection()
    {
    	ContextsIntersection(1, "simple" );
    }
    public void ContextsIntersection(int range, String mode)
    {

       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Contexts.Intersection");
       Push ( Integer.toString(range) );
       Push ( mode );
       AddFunction ();
       if (_DirectExecute)              DoIt();
    } 
    
    public void ContextsLearnContext()
    {

       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Contexts.LearnContext");
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }    
    
    public void ContextsNewFromAnalogs()
    {
    	ContextsNewFromAnalogs(true);
    }
    public void ContextsNewFromAnalogs (boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromAnalogs");
       Push (Boolean.toString(activated));

       AddFunction ();
       if (_DirectExecute)              DoIt();


    }

    public void ContextsNewFromAtoms()
    {
    	ContextsNewFromAtoms(1, false);
    }
    public void ContextsNewFromAtoms(int depth, boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromAtoms");
       Push (Integer.toString(depth));
       Push (Boolean.toString(activated));
       AddFunction ();
       if (_DirectExecute)              DoIt();


    }
    public void ContextsNewFromChilds()
    {
    	ContextsNewFromChilds(1, false);
    }
    public void ContextsNewFromChilds(int depth, boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromChilds");
       Push (Integer.toString(depth));
       Push (Boolean.toString(activated));
       AddFunction ();
       if (_DirectExecute)              DoIt();


    }
    public void ContextsNewFromComposite()
    {
    	ContextsNewFromComposite(1, false);
    }
    public void ContextsNewFromComposite(int depth, boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromComposite");
       Push (Integer.toString(depth));
       Push (Boolean.toString(activated));
       AddFunction ();
       if (_DirectExecute)              DoIt();


    }
    public void ContextsNewFromConnections()
    {
    	ContextsNewFromConnections(1, false);
    }
    public void ContextsNewFromConnections(int depth, boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromConnections");
       Push (Integer.toString(depth));
       Push (Boolean.toString(activated));
       AddFunction ();
       if (_DirectExecute)              DoIt();


    }
    public void ContextsNewFromCousins()
    {
    	ContextsNewFromCousins(false);
    }
    public void ContextsNewFromCousins(boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromCousins");
       Push (Boolean.toString(activated));
       AddFunction ();
       if (_DirectExecute)              DoIt();


    }
    public void ContextsNewFromLinks()
    {
    	ContextsNewFromLinks(1, false);
    }
    public void ContextsNewFromLinks(int depth, boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromLinks");
       Push (Integer.toString(depth));
       Push (Boolean.toString(activated));
       AddFunction ();
       if (_DirectExecute)              DoIt();


    }
    public void ContextsNewFromParent()
    {
    	ContextsNewFromParent(1, false);
    }
    public void ContextsNewFromParent(int depth, boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromParent");
       Push (Integer.toString(depth));
       Push (Boolean.toString(activated));
       AddFunction ();
       if (_DirectExecute)              DoIt();


    }
    public void ContextsNewFromUnKnownShape(String query)
    {
    	ContextsNewFromUnKnownShape(query, 1, false);
    }
    public void ContextsNewFromUnKnownShape(String query, int depth, boolean activated)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromUnKnownShape");
       Push ( query );
       Push (Integer.toString(depth));
       Push (Boolean.toString(activated));
       AddFunction ();
       if (_DirectExecute)              DoIt();


    }   
    
    public void ContextsNormalize()
    {
    	ContextsNormalize(Integer.MIN_VALUE, -1, 1);
    }
    public void ContextsNormalize(int threshold, int count, int minimum)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.Normalize");
       Push ( Integer.toString(threshold) );
       Push (Integer.toString(count));
       Push (Integer.toString(minimum));
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }     

    public void ContextsPartition()
    {
    	ContextsPartition(1,false,1,-1);
    }
    public void ContextsPartition(int min_size,boolean evaluate,int threshold, int count)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.Partition");
       Push ( Integer.toString(min_size) );
       Push ( Boolean.toString(evaluate) );
       Push (Integer.toString(threshold));
       Push (Integer.toString(count));
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }
    public void ContextsPredict(String query)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.Predict");
       Push ( query );
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }
    
    public void ContextsRollDown()
    {
    	ContextsRollDown(3);
    }
    public void ContextsRollDown(int range)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.RollDown");
       Push ( Integer.toString(range) );
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }
 
    public void ContextsRollUp()
    {
    	ContextsRollUp(3);
    }
    public void ContextsRollUp(int range)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.RollUp");
       Push ( Integer.toString(range) );

       AddFunction ();
       if (_DirectExecute)              DoIt();
    }

    public void ContextsSelectAll()
    {
      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.SelectAll");
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }
    
    
    public void ContextsSelectByIndex()
    {
    	ContextsSelectByIndex(1);
    }
    public void ContextsSelectByIndex(int range)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.SelectByIndex");
       Push ( Integer.toString(range) );
       AddFunction ();
       if (_DirectExecute)              DoIt();
    } 

    public void ContextsSelectByName(String name)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.SelectByName");
       Push ( name );
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }
    public void ContextsSelectByRange()
    {
    	ContextsSelectByRange(0,1);
    }
    public void ContextsSelectByRange(int range , int to)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.SelectByIndex");
       Push ( Integer.toString(range) );
       Push ( Integer.toString(to) );

       AddFunction ();
       if (_DirectExecute)              DoIt();
    }

    public void ContextsSelectReverse()
    {
      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.SelectReverse");
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }
    
    public void ContextsSetName(String name)
    {
      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.SetName");
       Push ( name );
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }    
    public void ContextsSplit()
    {
      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.Split");
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }  
 
    public void ContextsSwap()
    {
    	ContextsSwap(1);
    }
    public void ContextsSwap(int range )
    {
      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.Swap");
       Push(Integer.toString(range));
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }
    
    public void ContextsToResults()
    {
    	ContextsToResults(true,-1);
    }
    
    public void ContextsToResults(boolean intersect,int max_count)
    {
      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.ToResults");
       Push(Boolean.toString(intersect));
       Push(Integer.toString(max_count));
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }  
    
    public void ContextsUnion()
    {
    	ContextsUnion(1,"simple");
    }
    
    public void ContextsUnion(int range,String mode)
    {
      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.Union");
       Push(Integer.toString(range));
       Push(mode);
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }    
    public void ContextsUnSelect()
    {
      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.UnSelect");
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }
    
    //________________________________________________
    public void ContextsNewFromSem (boolean relation ,int max_act, int max_number)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.NewFromSem");
       Push (Boolean.toString(relation));
       Push (Integer.toString(max_act));
       Push (Integer.toString(max_number));
       AddFunction ();
       if (_DirectExecute)              DoIt();
    }

    //________________________________________________
    public void ContextsGetElements (int idx, int max_atoms)
    {

      if (_DirectExecute)              OpenScript(null);
      localParams.clear();
       Push ("Contexts.GetElements");
       Push (Integer.toString(idx));
       Push (Integer.toString(max_atoms));
       AddFunction ();
       if (_DirectExecute)
        {
          DoIt();
        }

    }

    //________________________________________________
    public int ContextsGetCount ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Contexts.GET");
       Push ("Count");
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
    	   String[] results = _result.GetDataByName("Count", -1);
           return Integer.getInteger(  results[0] );
        }
       
       return -1;

    }
    public void ResultsDup()
    {
    	ResultsDup(1);
    }
    
    public void ResultsDup ( int range )
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.Dup");
       Push (Integer.toString(range));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();

        }

    }
    public void ResultsSwap()
    {
    	ResultsSwap(1);
    }
    
    public void ResultsSwap ( int range )
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.Swap");
       Push (Integer.toString(range));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    public void ResultsDrop()
    {
    	ResultsDrop(1);
    }
    
    public void ResultsDrop ( int range )
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.Drop");
       Push (Integer.toString(range));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    public void ResultsRollUp()
    {
    	ResultsRollUp(3);
    }
    
    public void ResultsRollUp( int range )
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.RollUp");
       Push (Integer.toString(range));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }   
    public void ResultsRollDown()
    {
    	ResultsDrop(3);
    }
    
    public void ResultsRollDown( int range )
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.RollDown");
       Push (Integer.toString(range));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }

    public void ResultsNot()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.Not");
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    
    public void ResultsIntersection()
    {
    	ResultsIntersection(1);
    }
    public void ResultsIntersection(int range)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.Intersection");
       Push (Integer.toString(range));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    public void ResultsUnion()
    {
    	ResultsUnion(1);
    }
    public void ResultsUnion(int range)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.Union");
       Push (Integer.toString(range));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }   
    public void ResultsNew()
    {
    	ResultsUnion(100);
    }
    public void ResultsNew(int capacity)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.New");
       Push (Integer.toString(capacity));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }   
    public void ResultsSelectToTable()
    {
    	ResultsSelectToTable(0);
    }
    public void ResultsSelectToTable(int index)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.SelectToTable");
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }     
    public void ResultsSelectBy()
    {
    	ResultsSelectBy(0);
    }

    public void ResultsDeleteBy()
    {
    	ResultsSelectBy(0);
    }
    public void ResultsDeleteBy(int index)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.DeleteBy");
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    public void ResultsSortBy()
    {
    	ResultsSortBy(0);
    }
    public void ResultsSortBy(int index)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.SortBy");
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    public void ResultsGroupBy()
    {
    	ResultsGroupBy(0);
    }
    public void ResultsGroupBy(int index)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GroupBy");
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    public void ResultsUniqueBy()
    {
    	ResultsUniqueBy(0);
    }
    public void ResultsUniqueBy(int index)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.UniqueBy");
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    //________________________________________________
    public String ResultsGetOwnerTable (int idx)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.GetOwnerTable");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           String tmp;
           DoIt();
           String[] results = _result.GetDataByName("OwnerTable", -1);
           return results[0];
        }
       return null;

    }

    //________________________________________________
    public String ResultsGetSortedBy (int idx)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.GetSortedBy");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           String[] results = _result.GetDataByName("SortedBy", -1);
           return results[0];
        }
       return null;

    }

    //________________________________________________
    public String ResultsGetSortOrder (int idx)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.GetSortOrder");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           String[] results = _result.GetDataByName("SortOrder", -1);
           return results[0];

        }
       return null;

    }
*/
    
    public void ResultsSelectBy(String column, Boolean ascending)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.SelectBy");
       Push (column);
       Push(ascending.toString() );
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }  
    //________________________________________________
    public void ResultsSortBy (String colname,boolean ascending)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SortBy");
       Push (colname);
       Push (Boolean.toString(ascending));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }



    //________________________________________________


    //________________________________________________

    //________________________________________________
    public void ResultsFetch (String lines, String start)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.Fetch");

           Push (lines);  
           Push (start);
            

       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    /*
    public void ResultsFetchGroup()
    {
    	ResultsFetchGroup(0,10,1);
    }
    //________________________________________________
    public void ResultsFetchGroup (int idx, int lines, int start)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.FetchGroup");
       Push (Integer.toString(idx));
       if (lines > 0)
        {
           Push (Integer.toString(lines));
           if (start > 0)
             {
               Push (Integer.toString(start));
             }
        }

       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }

    public void ResultsAdd()
    {
    	ResultsAdd(0,-1,-1);
    }
    //________________________________________________
    public void ResultsAdd (int index,int dbid, int activity)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.Add");
       Push (Integer.toString(index));
       Push (Integer.toString(dbid));
       Push (Integer.toString(activity));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    } 
    
    public void ResultsNormalize()
    {
    	ResultsNormalize(0,"absolute",-1);
    }
    //________________________________________________
    public void ResultsNormalize (int index, String mode, int max_activity)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.Add");
       Push (Integer.toString(index));
       Push (mode);
       Push (Integer.toString(max_activity));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    } 
    public void ResultsLockRead()
    {
    	ResultsLockRead(0);
    }
    //________________________________________________
    public void ResultsLockRead(int index)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.LockRead");
       Push (Integer.toString(index));

       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }    
    public void ResultsUnLockRead()
    {
    	ResultsUnLockRead(0);
    }
    //________________________________________________
    public void ResultsUnLockRead(int index)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.UnLockRead");
       Push (Integer.toString(index));

       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void ResultsLockWrite()
    {
    	ResultsLockWrite(0);
    }
    //________________________________________________
    public void ResultsLockWrite(int index)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.LockWrite");
       Push (Integer.toString(index));

       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void ResultsUnLockWrite()
    {
    	ResultsUnLockWrite(0);
    }
    //________________________________________________
    public void ResultsUnLockWrite(int index)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.UnLockWrite");
       Push (Integer.toString(index));

       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void ResultsToContext()
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.ToContext");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void ResultsShrink()
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.Shrink");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    
    public void ResultsAmplify()
    {
    	ResultsAmplify(1., Double.MIN_VALUE );
    }
    public void ResultsAmplify(double gain, double max_value)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.Amplify");
       Push (Double.toString(gain));
       Push (Double.toString(max_value));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    

    public void ResultsClear()
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.Clear");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    
    public void ResultsSetMaxStackSize()
    {
    	ResultsSetMaxStackSize(16);
    }
    
    public void ResultsSetMaxStackSize(int max_stack_size)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetMaxStackSize");
       Push (Integer.toString(max_stack_size));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void ResultsSetOwnerTable(String name)
    {
    	ResultsSetOwnerTable(name,0);
    }
    
    public void ResultsSetOwnerTable(String table, int index)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetOwnerTable");
       Push( table );
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void ResultsSetResultCapacity()
    {
    	ResultsSetResultCapacity(32,0);
    }
    
    public void ResultsSetResultCapacity(int capacity, int index)
    {
       //si lines <= 0 valeurs par d�faut, fetchsize et fetchsttart
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetResultCapacity");
       Push( Integer.toString(capacity));
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void ResultsSetFetchSize()
    {
    	ResultsSetFetchSize(32,0);
    }
    public void ResultsSetFetchSize (int size,int index)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetFetchSize");
       Push (Integer.toString(size));
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    
    public void ResultsSetFetchStart()
    {
    	ResultsSetFetchStart(1,0);
    }
    public void ResultsSetFetchStart (int start,int index)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetFetchStart");
       Push (Integer.toString(start));
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    
    public void ResultsSetEmbedded()
    {
    	ResultsSetEmbedded(true);
    }
    public void ResultsSetEmbedded (boolean embedded)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetEmbedded");
       Push (Boolean.toString(embedded));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }  
  
    public void ResultsSetColSep()
    {
    	ResultsSetColSep(",");
    }
    public void ResultsSetColSep (String separator)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetColSep");
       Push (separator);
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    } 
    public void ResultsSetLineSep()
    {
    	ResultsSetLineSep("CRLF");
    }
    public void ResultsSetLineSep (String separator)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetLineSep");
       Push (separator);
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    } 
    public void ResultsSetAutoLock()
    {
    	ResultsSetAutoLock(false, 0);
    }
    public void ResultsSetAutoLock ( boolean auto_lock, int index)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetAutoLock");
       Push (Boolean.toString(auto_lock));
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    } 
    */

    public void ResultsSetFormat ( String format)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetFormat");
       Push ( format );
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }   

    /*
    public void ResultsSetName(String name)
    {
    	ResultsSetName(name, 0);
    }
    public void ResultsSetName ( String name, int index)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.SetName");
       Push ( name );
       Push (Integer.toString(index));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }   

    public void ResultsGetMaxStackSize ( )
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.GetMaxStackSize");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    
    //________________________________________________
    public int ResultsGetCount ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GET");
       Push ("Count");
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
    	   String[] results = _result.GetDataByName("Count", -1);
           return Integer.getInteger(  results[0] );
        }
       else
    	   return -1;

    }

    public void ResultsGetEmbedded ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GetEmbedded");
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();

        }

    }   
    public void ResultsGetColSep ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GetColSep");
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();

        }

    }  
    public void ResultsGetLineSep ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GetLineSep");
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    public void ResultsGetAutoLock ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GetAutoLock");
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
        }
    }
    
    //________________________________________________
    public void ResultsGetStack ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GET");
       Push ("Stack");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    
    public int ResultsGetFetchStart()
    {
    	return ResultsGetFetchStart(0);
    }
    //________________________________________________
    public int ResultsGetFetchStart (int idx)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GET");
       Push ("FetchStart");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           String[] results = _result.GetDataByName("FetchStart", -1);
           return Integer.getInteger( results[0]   );
        }
       else                                 
    	   return -1;

    }

    //________________________________________________
    public int ResultsGetFetchSize (int idx)
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Results.GET");
       Push ("GetFetchSize");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
       {
          DoIt();
          String[] results = _result.GetDataByName("FetchSize", -1);
          return Integer.getInteger(  results[0] );
       }
      else                                 
   	   return -1;
    }
    
    // méthodes de l'objet SERVER
    public void ServerShutDown ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Server.ShutDown");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();

        }
    }
    public void ServerProperties ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Server.Properties");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();

        }
    }   
    public void ServerSetCommandThreads()
    {
    	ServerSetCommandThreads(16);
    }
    public void ServerSetCommandThreads(int number)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Server.SetCommandThreads");
        Push( Integer.toString(number));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void ServerSetCacheSize()
    {
    	ServerSetCacheSize(16000);
    }
    public void ServerSetCacheSize(int size_in_KB)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Server.SetCacheSize");
        Push( Integer.toString(size_in_KB));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }  
    public void ServerSetExecTimeOutDefault()
    {
    	ServerSetExecTimeOutDefault(5000);
    }
    public void ServerSetExecTimeOutDefault(int time_ms)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Server.SetExecTimeOutDefault");
        Push( Integer.toString(time_ms));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void ServerSetSessionTimeOutDefault()
    {
    	ServerSetSessionTimeOutDefault(-1);
    }
    public void ServerSetSessionTimeOutDefault(int time_ms)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Server.SetSessionTimeOutDefault");
        Push( Integer.toString(time_ms));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void ServerGetConnected()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Server.GetConnected");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void ServerGetAPI()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Server.GetAPI");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }   
    public void ServerGetTasks()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Server.GetTasks");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    } 

    //________________________________________________
    public String ServerGetName ()
    {
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Server.GetName");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           String[] results = _result.GetDataByName("Name", -1);
           return results[0];
        }
                          
    return null;

    }

    // méthodes de l'objet Contexts
    
    public void ContextsSortByGenerality()
    {
    	ContextsSortByGenerality(true, 0);
    }
    //________________________________________________
    public void ContextsSortByGenerality(boolean order,int idx)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.SortByGenerality");
       Push (Boolean.toString(order));
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           return ;
        }
       else                         return ;

    }

    //________________________________________________
    public void ContextsSortByActivity(boolean order,int idx)
    {

       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.SortByActivity");
       Push (Boolean.toString(order));
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();

        }
    }

    //________________________________________________
    public boolean ContextsGetSortOrder(int idx)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.GetSortOrder");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
    	   DoIt();
    	   String[] results = _result.GetDataByName("SortOrder", -1);
           return Boolean.getBoolean( results[0] );
        }
       return false;

    }
    //________________________________________________
    public String ContextsGetSortedBy (int idx)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.GetSortedBy");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           String[] results = _result.GetDataByName("SortedBy", -1);

           return results[0];
        }
     return null;

    }

    //________________________________________________
    public int ContextsGetAct (int idx)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.GetAct");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           String[] results = _result.GetDataByName("Act", -1);
           return Integer.getInteger( results[0] );
        }
       else                         return -1;

    }
    //________________________________________________
    public int ContextsGetSize (int idx)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Contexts.GetSize");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           String[] results = _result.GetDataByName("Atoms", -1);
           return Integer.getInteger( results[0] );

        }
       else                         return -1;

    }

    //________________________________________________
    public int ResultsGetSize (int idx)
    {
       if (_DirectExecute)     OpenScript(null);
       localParams.clear();
       Push ("Results.Get");
       Push ("ResultCount");
       Push (Integer.toString(idx));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
           String[] results = _result.GetDataByName("ResultCount", -1);
           return Integer.getInteger( results[0] );
        }
       return -1;

    }
    */
    
    public void TableCreate (String name, String owner, String dir, String size, String type, String descriptor )
    {
       if (name == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       Push ("Table.Create");
       Push(name);
       Push(owner);
       Push(dir);
       Push(size);
       Push( type );
       Push( descriptor );
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    } 
    
    public void TableKUnIndex(String name, String row_id)
    {

	       if (_DirectExecute)             OpenScript(null);
	       localParams.clear();
	       Push ("Table:"+name+".KUnIndex");
	       Push(name);
	       Push(row_id);
	       AddFunction ();
	       if (_DirectExecute)
	        {
	           DoIt();
	        }
	
    }

    public void TableKReIndex(String name, String row_id)
    {

	       if (_DirectExecute)             OpenScript(null);
	       localParams.clear();
	       Push ("Table:"+name+".KReIndex");
	       Push(name);
	       Push(row_id);
	       AddFunction ();
	       if (_DirectExecute)
	        {
	           DoIt();
	        }
	
    } 
    
    public void TableDelete (String tbl, ArrayList<String> ids)
    {
    	String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Delete";
       Push(tmp);
       for (int i = 0; i < ids.size();i++)
       {
    	   Push(ids.get(i));
       }
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
  
    /*
    public void TableDataAdd(String tbl,String column_name, String value)
    {
    	TableDataAdd(tbl, -1,column_name, value );
    }
    public void TableDataAdd (String tbl, int db_id, String column_name, String value )
    {
    	String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".DataAdd";
       Push(tmp);
       Push (Integer.toString(db_id));
       Push (column_name);
       Push( value );
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    */

    public void TableUpdate (String tbl, int db_id, String[] value )
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Update";
       Push(tmp);
       Push (Integer.toString(db_id));
       for (int i =0; i < value.length -1;i ++)
       {
	   Push( value[i]);
	   Push( value[i + 1]);
       }
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }

    public void TableInsert (String tbl,  String value [])
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Insert";
       Push(tmp);
       for (int i = 0; i < value.length - 1; i+=2){
	   Push( KMString.trim(value[i].toLowerCase()));
	   Push( KMString.trim(value[i+1].toLowerCase()));
       }
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }

  /*  
    public void TableSelect(String tbl,String column_name)
    {
    	TableSelect(tbl,"",column_name,"","","", 100000 , 0);
    }
    public void TableSelect (String tbl, String mode, String column_name, String operation, String op1, String op2, int size, int position )
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Select";
       Push(tmp);
       Push( mode );
       Push( operation );
       Push( op1 );
       Push( op2 );
       Push (Integer.toString(size));
       Push (Integer.toString(position));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
   
    public void TableBIndexCreate(String tbl,String column_name)
    {
    	TableBIndexCreate(tbl,column_name,false,"", -1);
    }
    public void TableBIndexCreate (String tbl, String column_name, boolean unique, String map_table, int size )
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".BIndexCreate";
       Push(tmp);
       Push( column_name );
       Push( Boolean.toString(unique) );
       Push( map_table );
       Push (Integer.toString(size));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    
    public void TableBIndexDelete (String tbl, String column_name )
    {
    	String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".BIndexDelete";
       Push(tmp);
       Push( column_name );
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    public void TableBIndexRebuild (String tbl, String column_name )
    {
    	String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".BIndexRebuild";
       Push(tmp);
       Push( column_name );
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    public void TableLockWrite(String tbl, ArrayList<Integer> ids )
    {
    	TableLockWrite(tbl, -1, ids);
    }
    
    public void TableLockWrite(String tbl, int db_id, ArrayList<Integer> ids )
    {
    	String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".LockWrite";
       Push(tmp);
       Push( Integer.toString(db_id) );
       for (int i = 0; i < ids.size();i++)
       {
    	   Push( ids.get(i).toString() );
       }
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    public void TableUnLockWrite(String tbl, ArrayList<Integer> ids )
    {
    	TableUnLockWrite(tbl, -1, ids);
    }
    
    public void TableUnLockWrite(String tbl, int db_id, ArrayList<Integer> ids )
    {
    	String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".UnLockWrite";
       Push(tmp);
       Push( Integer.toString(db_id) );
       for (int i = 0; i < ids.size();i++)
       {
    	   Push( ids.get(i).toString() );
       }
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    
    public void TableLockRead(String tbl, ArrayList<Integer> ids )
    {
    	TableLockRead(tbl, -1, ids);
    }
    
    public void TableLockRead(String tbl, int db_id, ArrayList<Integer> ids )
    {
    	String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".LockRead";
       Push(tmp);
       Push( Integer.toString(db_id) );
       for (int i = 0; i < ids.size();i++)
       {
    	   Push( ids.get(i).toString() );
       }
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    public void TableUnLockRead(String tbl, ArrayList<Integer> ids )
    {
    	TableUnLockRead(tbl, -1, ids);
    }
    
    public void TableUnLockRead(String tbl, int db_id, ArrayList<Integer> ids )
    {
    	String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".UnUnLockRead";
       Push(tmp);
       Push( Integer.toString(db_id) );
       for (int i = 0; i < ids.size();i++)
       {
    	   Push( ids.get(i).toString() );
       }
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
   
    
    
    public void TableReadBlock(String tbl,String column_name)
    {
    	TableReadBlock(tbl,-1,column_name,1, 4096);
    }
 
    public void TableReadBlock (String tbl, int db_id, String column_name, int position, int size )
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".ReadBlock";
       Push(tmp);
       Push ( Integer.toString(db_id));
       Push( column_name );
       Push (Integer.toString(position));
       Push (Integer.toString(size));
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    
    public void TableReadLine(String tbl,String column_name)
    {
    	TableReadLine(tbl,-1);
    }
    public void TableReadLine (String tbl, int db_id )
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".ReadLine";
       Push(tmp);
       Push ( Integer.toString(db_id));

       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    
    public void TableReadFirstLine (String tbl )
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".ReadFirstLine";
       Push(tmp);
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    
    public void TableReadNextLine (String tbl )
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".ReadNextLine";
       Push(tmp);
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    
    public void TableReadContext(String tbl)
    {
    	TableReadContext(tbl, -1, 1);
    }
    public void TableReadContext (String tbl, int db_id, int threshold )
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".ReadContext";
       Push(tmp);
       Push ( Integer.toString(db_id));
       Push ( Integer.toString(threshold));

       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    
    //________________________________________________
    public void TableProperties (String tbl)
    {
       String tmp;
       if (tbl == null) return;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".properties";
       Push (tmp);
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }
    }
    public void TableGetLines (String tbl)
    {
       String tmp;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Get";
       Push (tmp);
       Push ("Lines");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void TableGetOwner (String tbl)
    {
       String tmp;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Get";
       Push (tmp);
       Push ("Owner");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    //________________________________________________
    public void TableGetStructure (String tbl)
    {
       String tmp;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Get";
       Push (tmp);
       Push ("Structure");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }
    public void TableGetBIndexes (String tbl)
    {
       String tmp;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Get";
       Push (tmp);
       Push ("BIndexes");
       AddFunction ();
       if (_DirectExecute)
        {
           DoIt();
        }

    }

    //________________________________________________


    //________________________________________________



    //________________________________________________
    public void TableSelect (
                                            String tbl,
                                            String mode,
                                            String col,
                                            String op,
                                            String param1,
                                            String param2
                                            )
    {
      


       String tmp;
       if (_DirectExecute)             OpenScript(null);
       localParams.clear();
       tmp = "Table:";
       tmp += tbl;
       tmp += ".Select";
       Push (tmp);
       Push (mode);
       Push (col);
       Push (op);
       Push (param1);
       Push (param2);
       AddFunction ();
       if (_DirectExecute)
        {
          DoIt();
        }

    }

    
    public void SessionConnect()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Connect");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    } 
    
    public void SessionScript()
    {
    	SessionScript(-1);
    }
    public void SessionScript(int session_id)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Script");
        Push( Integer.toString(session_id));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void SessionPassWord()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.PassWord");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionKill()
    {
    	SessionKill(-1);
    }
    public void SessionKill(int session_id)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Kill");
        Push( Integer.toString(session_id));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void SessionBreak()
    {
    	SessionBreak(-1, "false");
    }
    public void SessionBreak(int session_id, String _break)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Break");
        Push(_break);
        Push( Integer.toString(session_id));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void SessionClearSession()
    {
    	SessionClearSession(-1);
    }
    public void SessionClearSession(int session_id)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.ClearSession");
        Push( Integer.toString(session_id));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void SessionProgress()
    {
    	SessionProgress(-1);
    }
    public void SessionProgress(int session_id)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Progress");
        Push( Integer.toString(session_id));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionProps()
    {
    	SessionProps(-1);
    }
    public void SessionProps(int session_id)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Props");
        Push( Integer.toString(session_id));
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }  
    public void SessionGetInstances()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Get");
        Push("Instances");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    } 
    public void SessionGetAverageTime()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Get");
        Push("AverageTime");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionPropertyAdd(String prop_name, String value)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        String tmp = "Session:"+prop_name+".Add";
        Push (tmp);
        Push( value );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionPropertyDelete(String prop_name)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        String tmp = "Session:"+prop_name+".Delete";
        Push (tmp);
        Push( prop_name );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionPropertySet(String prop_name,String value)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        String tmp = "Session:"+prop_name+".Set"; 
        Push (tmp);
        Push( value );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionPropertyGet(String prop_name)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();             
        String tmp = "Session:"+prop_name+".Get";                     
        Push (tmp);
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionClear()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Clear");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void SessionEventCatch()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.EventCatch");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void SessionLockKnowledge()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.LockKnowledge");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void SessionDoScript(String script)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.DoScript");
        Push( script );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void SessionSetKnowledge(String knw)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Set");
        Push( "Knowledge" );
        Push( knw );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionSetPriority()
    {
    	SessionSetPriority(3);
    }
    public void SessionSetPriority(int level )
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Set");
        Push( "Priority" );
        Push( Integer.toString(level) );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionSetExecTimeOut()
    {
    	SessionSetExecTimeOut(-1);
    }
    public void SessionSetExecTimeOut(int time_ms )
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Set");
        Push( "ExecTimeOut" );
        Push( Integer.toString(time_ms) );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionSetSessionTimeOut()
    {
    	SessionSetExecTimeOut(-1);
    }
    public void SessionSetSessionTimeOut(int time_ms )
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Set");
        Push( "SessionTimeOut" );
        Push( Integer.toString(time_ms) );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionSetPersistant()
    {
    	SessionSetPersistant("false");
    }
    public void SessionSetPersistant(String persistant  )
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Set");
        Push( "Persistant" );
        Push( persistant );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionSetSessionName()
    {
    	SessionSetSessionName("");
    }
    public void SessionSetSessionName(String name  )
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Set");
        Push( "SessionName" );
        Push( name );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionSetEvent()
    {
    	SessionSetEvent("false");
    }
    public void SessionSetEvent(String event  )
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Set");
        Push( "Event" );
        Push( event );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionGetLastTime()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Get");
        Push( "LastTime" );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void SessionGetId()
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
        Push ("Session.Get");
        Push( "Id" );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    // méthodes de l'objet ASCIITABLE
    
    public void ASCIITableCreate(String name, String owner, String dir, String data)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
    Push ("ASCIITable.Create");
    Push( name);
    Push( owner );
    Push( dir);
    Push( data );
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    public void ASCIITableKill(String name)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
    Push ("ASCIITable.Kill");
    Push( name);

        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void ASCIITableRename(String name, String newname)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
    Push ("ASCIITable.Rename");
    Push( name);
    Push( newname);
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }
    
    public void ASCIITableGetInstances(String name, String newname)
    {
        if (_DirectExecute)             OpenScript(null);
        localParams.clear();
    Push ("ASCIITable.Get");
    Push( "Instances");
        AddFunction ();
        if (_DirectExecute)
         {
            DoIt();

         }
    }*/
    
    public void Push(String s)
    {
      localParams.add( new String(s) );
    }
    
}
