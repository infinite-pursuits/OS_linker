//package my_linker;
import java.io.*;
import java.util.*;

public class oslinker{
	private static int mod_base_add[]=null;
	private static int no_of_mods=0;
	private static Scanner s=null;
	private static Scanner s2=null;
	private static HashMap<String, Integer> symbol_Table=null;
	private static HashMap<String, Integer> symbol_Table_dummy=null;
	private static Set<String> global_use_list = null;
	private static int machine_size=200;
	private static Set<String> globally_used = null;
	
public static void main(String[] args)
{	
	InputStream input=System.in;
	input.mark(0);
	s=new Scanner(input);
	no_of_mods=s.nextInt();
	
	mod_base_add=new int[no_of_mods];
	symbol_Table = new HashMap<String, Integer>();
	symbol_Table_dummy = new HashMap<String, Integer>();
	
	if(no_of_mods>0)
		{first_pass();
		try{
			input.reset();
			s2=new Scanner(input);
			s2.nextInt();
		}
		catch(IOException e){
			System.out.println("Can't reset the cursor to the marked position.");
		}
		
		second_pass();
		}
	else
		System.out.println("Number of modules is zero");
	
	
}

private static void first_pass()
	{	 int no_of_defs=0,no_of_uses=0,no_of_insts=0,symbol_relative_address=0,module_size=0,no_of_modss=no_of_mods;
		String symbol;
		int i=0;
		mod_base_add[i]=0;
		
		while(no_of_modss>0)
		{
			no_of_defs=s.nextInt();
			for(;no_of_defs>0;no_of_defs--)
			{	symbol=s.next();
				symbol_relative_address=s.nextInt();
				if(symbol_Table_dummy.containsKey(symbol))
				{
					System.out.println("\nError: Symbol "+symbol+" is multiply defined. First definition used.");
				}
				else
				{	
					symbol_Table_dummy.put(symbol, symbol_relative_address+mod_base_add[i]);
				}	
			}
			
			no_of_uses=s.nextInt();
			for(;no_of_uses>0;no_of_uses--)
			{
				s.next();
			}
			
			no_of_insts=s.nextInt();
			module_size=no_of_insts;
			for(Map.Entry<String,Integer> entry : symbol_Table_dummy.entrySet())
			{
				if(entry.getValue()>no_of_insts+mod_base_add[i])
				{	
					System.out.println("\nError: Symbol "+entry.getKey()+"'s relative address exceeds the size of the module.");
					entry.setValue(mod_base_add[i]);
				}
				
			}
			
			
			for(;no_of_insts>0;no_of_insts--)
			{
				s.next();
				s.next();
			}
			
			int p=i+1;
			no_of_modss--;
			if(no_of_modss>0)
			{	mod_base_add[p]=module_size+mod_base_add[i];
				i++;}
		}
		
		symbol_Table=symbol_Table_dummy;
		System.out.println("\nSymbol Table");
		for(Map.Entry<String,Integer> entry : symbol_Table.entrySet())
			System.out.println(entry.getKey()+" : "+entry.getValue());
		
		
	}

private static void second_pass()
{
	int no_of_defs=0,no_of_uses=0,no_of_insts=0,module_size=0;
	String string_temp=null;
	int address; int int_temp=0; int line=0;
	global_use_list= new HashSet<String>();
	ArrayList<String> module_use_list=null;
	Set<String> symbols_used_in_module = null;
	globally_used= new HashSet<String>();	
	int i=0;
	int no_of_modss=no_of_mods;
	System.out.println("\nMemory Map");
	while(no_of_modss>0)
	{	symbols_used_in_module = new HashSet<String>();
		module_use_list=new ArrayList<String>();
		no_of_defs=s2.nextInt();
		for(;no_of_defs>0;no_of_defs--)
		{	s2.next();
			s2.nextInt();}
		
		no_of_uses=s2.nextInt();
		for(;no_of_uses>0;no_of_uses--)
		{	string_temp=s2.next();
		global_use_list.add(string_temp);
			module_use_list.add(string_temp);
		}
		
		module_size=no_of_insts=s2.nextInt();
		for(;no_of_insts>0;no_of_insts--)
		{
			string_temp=s2.next();
			
			int_temp=s2.nextInt();
			address=int_temp%1000;

			if(string_temp.equals("A"))
			{
				if(address>machine_size)
				{
					System.out.println(line+": "+int_temp/1000+"000"+" Error: Absolute address exceeds machine size; zero used.");
				}
				else
				{
					System.out.println(line+": "+int_temp);
				}
			}
			
			if(string_temp.equals("I"))
			System.out.println(line+": "+int_temp);
				
			if(string_temp.equals("R"))
			{
				if(address>module_size)
				{
					System.out.println(line+": "+int_temp/1000+"000"+" Error: Relative address exceeds module size; zero used.");
				}
				else
				{
					System.out.println(line+": "+(int_temp+mod_base_add[i]));
				}
			}
			
			if(string_temp.equals("E"))
			{
				if(address>module_use_list.size())
				{
					System.out.println(line+": "+int_temp+" Error: External address too large to reference an entry in the use list; used as immediate.");
				}
				else
				{	if(symbol_Table.containsKey(module_use_list.get(address)))
					{System.out.println(line+": "+((int_temp-address)+symbol_Table.get(module_use_list.get(address))));
					symbols_used_in_module.add(module_use_list.get(address));
					globally_used.add(module_use_list.get(address));
					}
				else
					System.out.println(line+": "+int_temp/1000+"000"+" Error: "+(module_use_list.get(address))+" is not defined but is used. Zero used.");
				
					}
				
					
				}
			line++;
			
		}
		if (!module_use_list.isEmpty())
		{
			for (String string_temp2:module_use_list)
			{	
				if (symbol_Table.containsKey(string_temp2) && !symbols_used_in_module.contains(string_temp2))
					System.out.println("Warning: In module "+i+", symbol "+string_temp2+" appeared in the use list but was not actually used.");
			}
		}
	

		i++;
		no_of_modss--;
	}
	if (!symbol_Table.isEmpty()){
		int j=0,flag=0;
		for (Map.Entry<String,Integer> entry : symbol_Table.entrySet())
		{	flag=0;
			if (!globally_used.contains(entry.getKey()))
			{	for(j=0;j<no_of_mods-1;j++)
				{
				if(entry.getValue()>=mod_base_add[j] && entry.getValue()<mod_base_add[j+1])
					{
					flag=1;
					break;
				}}
			if(flag==0)
			{
				j=no_of_mods-1;
			}
			
				System.out.println("Warning: "+entry.getKey()+" was defined in module "+j+" but never used.\n");
			}
		}
	}
}

}

