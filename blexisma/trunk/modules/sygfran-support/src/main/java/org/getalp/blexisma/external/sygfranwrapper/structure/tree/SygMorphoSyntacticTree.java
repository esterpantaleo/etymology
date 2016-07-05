package org.getalp.blexisma.external.sygfranwrapper.structure.tree;

import java.util.ArrayList;
import java.util.Stack;

import org.getalp.blexisma.external.sygfranwrapper.structure.info.SygMorphoSyntacticInformations;
import org.getalp.blexisma.utils.Patterner;

/**
 * @author Alexandre Labadié, Didier Schwab
 * 
 * Morpho-syntactic tree for Sygfran
 * */
public class SygMorphoSyntacticTree 
{
	private SygMorphoSyntacticInformations infos;
	private ArrayList<SygMorphoSyntacticTree> children;
	
	public SygMorphoSyntacticTree(SygMorphoSyntacticInformations infos)
	{
	    this.infos=infos;
	}
	
	public SygMorphoSyntacticTree(String infos, ArrayList<SygMorphoSyntacticTree> children)
	{
	    this.children = children;
	    this.infos=new SygMorphoSyntacticInformations();
	    fillInfos(infos);
	  }
	
	@SuppressWarnings("unchecked")
	public SygMorphoSyntacticTree(String sygana)
	{
		Object[] t = null;
		String anachaine = Patterner.patterner(sygana, "\\s+", "");
		anachaine.trim();
		
		t = format(anachaine);
		
		children = buildTree((String)t[0],(ArrayList<String>)t[1]);
		
		infos = new SygMorphoSyntacticInformations();
		infos.setLEMME("ROOT");
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<SygMorphoSyntacticTree> buildTree(String struct, ArrayList<String> etiq)
	{
		ArrayList<SygMorphoSyntacticTree> tree = new ArrayList<SygMorphoSyntacticTree>();
		int index = 1;
		//int noeud = 0;
		String buff = "";
		Character c;
		@SuppressWarnings("rawtypes")
		Stack pile = new Stack();
		
		pile.push(new Character(struct.charAt(0)));
		
		while(!pile.empty())
		{
			      c = new Character(struct.charAt(index));

			      if(c.charValue()!=')')
			      {
			        pile.push(c);
			        index++;
			        c = new Character(struct.charAt(index));
			      }
			      else
			        if(pile.peek() instanceof SygMorphoSyntacticTree)
			        {
			          tree.add(0, (SygMorphoSyntacticTree)pile.pop());
			          
			          if(pile.peek() instanceof Character && ((Character)pile.peek()).charValue()=='(')
			          {
			            buff="";
			            index++;
			            pile.pop();
			            c = (Character)pile.pop();
			            while(!pile.empty() && c.charValue() != ',' && c.charValue() != '(')
			            {
			               buff = c.charValue() + buff;
			               c = (Character)pile.pop();
			            }
			            if(c.charValue() == '(')
			              pile.push(c);
			            if(!pile.empty())
			            {
			              pile.push(new SygMorphoSyntacticTree((String)etiq.get(Integer.parseInt(buff)-1), tree));
			              tree = new ArrayList<SygMorphoSyntacticTree>();
			              buff="";
			            }
			            else
			              return tree;
			          }
			          else
			            if(pile.peek() instanceof Character && ((Character)pile.peek()).charValue()==',')
			              pile.pop();
			        }
			        else
			          if(((Character)pile.peek()).charValue()!=',' && ((Character)pile.peek()).charValue()!='(')
			          {
			            buff=((Character)pile.pop()).charValue() + buff;
			          }
			          else
			            if(((Character)pile.peek()).charValue()=='(')
			            {
			              if(!buff.equals(""))
			            	  tree.add(0,new SygMorphoSyntacticTree((String)etiq.get(Integer.parseInt(buff)-1), null));
			              buff="";
			              pile.pop();
			              index++;
			              c = (Character)pile.pop();
			              while(c.charValue() != ',' && c.charValue() != '(')
			              {
			                buff = c.charValue() + buff;
			                c = (Character)pile.pop();
			              }
			              pile.push(c);
			              pile.push(new SygMorphoSyntacticTree((String)etiq.get(Integer.parseInt(buff)-1), tree));
			              buff="";
			              tree = new ArrayList<SygMorphoSyntacticTree>();
			            }
			            else
			            {
			                tree.add(0,new SygMorphoSyntacticTree((String)etiq.get(Integer.parseInt(buff)-1), null));
			                pile.pop();
			                buff="";
			            }
			    }
			    return null;
	}
	
	private Object[] format(String anachaine)
	{
		Object[] res = new Object[2];

	    String elem = "ELEM(VariableAnalyseSyntaxique,STR([1](";
	    String VTQ = "VTQ";
	    String etiq = "),NOM_ETIQUETTES())";
	    int index = anachaine.indexOf(VTQ, elem.length());

	    int index2 = index+VTQ.length();

	    res[0] = anachaine.substring(elem.length(), index);
	    res[1] = vectorize(anachaine.substring(index2, anachaine.indexOf(etiq, index2)));
	    return res;
	}
	
	private void fillInfos(String inf)
	{
		this.infos.BAL = find(inf, "Balise");
		this.infos.GNR = find(inf, "GNR");
	    this.infos.NUM = find(inf, "NUM");
	    this.infos.CAT = find(inf, "CAT");
	    this.infos.FRM = find(inf, "FRM");
	    this.infos.LEMME = find(inf, "LEMME");
	    this.infos.FS = find(inf, "FS");
	    this.infos.SOUSV = find(inf, "SOUSV");
	    this.infos.SOUSA = find(inf, "SOUSA");
	    this.infos.SOUSN = find(inf, "SOUSN");
	    this.infos.SOUSD = find(inf, "SOUSD");
	    this.infos.SOUSR = find(inf, "SOUSR");
	    this.infos.SOUSC = find(inf, "SOUSC");
	    this.infos.SOUSP = find(inf, "SOUSP");
	    this.infos.ASSERT = find(inf, "ASSERT");
	    this.infos.VECTS = find(inf,"VECTEUR_SEMANTIQUE");
	}
	
	private String find(String findIn, String toFind)
	{
	    int deb = findIn.indexOf(toFind+'(');
	    if(deb>=0)
	    {
	      deb+=toFind.length()+1;
	      int fin = findIn.indexOf(')', deb);
	      return Patterner.patterner(findIn.substring(deb, fin), "\\x20", " ");
	    }
	    else
	      return null;
	  }
	
	private ArrayList<String> vectorize(String anachaine)
	{
		ArrayList<String> res = new ArrayList<String>();
		String infos = "";
		String noeud = "";
		char state = 'S';
		int d = 0;
		
		for (int i=0; i<anachaine.length();i++)
		{
			switch(state)
			{
				case('S')://Etat initial
				{
					switch(anachaine.charAt(i))
					{
		            	case('('):
		            	{
		            		state = 'N';
		            		break;
		            	}
		            	default: return null;
					}
					break;
				}
				case('N')://Recherche de l'id du noeud
				{
					switch(anachaine.charAt(i))
					{
			            case(' '):
			            case(','): break;
			            case('('):
			            {
			              d++;
			              state = 'I';
			              break;
			            }
			            case('0'):
			            case('1'):
			            case('2'):
			            case('3'):
			            case('4'):
			            case('5'):
			            case('6'):
			            case('7'):
			            case('8'):
			            case('9'):
			            {
			              noeud+=anachaine.charAt(i);
			              break;
			            }
			            default:
			            {
			              System.out.println("Problème de char " + anachaine.substring(i, anachaine.length()));
			              return null;
			            }
					}
					break;
				}
				case('I')://Parcours des infos du noeud
				{
					switch(anachaine.charAt(i))
					{
			            case(' '): break;
			            case('\\'):
			            {
			              state = 'B';
			              break;
			            }
			            case(')'):
			            {
			              d--;
			              if(d==0)
			              {
			            	res.add(Integer.parseInt(noeud,10)-1, infos);
			                infos="";
			                noeud="";
			                state = 'N';
			              }
			              else
			                infos += anachaine.charAt(i);
			              break;
			            }
			            case('('):
			            {
			              infos += anachaine.charAt(i);
			              d++;
			              break;
			            }
			            default:
			            {
			              infos += anachaine.charAt(i);
			            }
					}
					break;
				}
				case('B')://Cas particulier de caractère de rupture
				{
					infos += anachaine.charAt(i);
			        state = 'I';
					break;
				}
			}
		}
		
		return res;
	}
	
	
	public boolean isLeaf()
	{
	    return (children == null || children.size()==0 || !(children.get(0) instanceof SygMorphoSyntacticTree));
	}
	
	public ArrayList<SygMorphoSyntacticTree> getLeaves()
	{
		ArrayList<SygMorphoSyntacticTree> rep = new ArrayList<SygMorphoSyntacticTree>();

	    if(isLeaf())
	    {
	      rep.add(this);
	      return rep;
	    }
	    else{
	      for(int i = 0; i < children.size(); i++)
	        rep.addAll(getChild(i).getLeaves());
	      return rep;
	    }
	  }
	
	public int getNumberOfChilds()
	{
	    if(isLeaf())
	      return 0;
	    else
	      return children.size();
	  }
	
	public SygMorphoSyntacticTree getChild(int i)
	{

	    if(isLeaf())
	      return null;
	    else
	      return children.get(i);
	  }
	
	public void addChild(SygMorphoSyntacticTree tree)
	{
	    if(children==null)
	      children=new ArrayList<SygMorphoSyntacticTree>();
	    children.add(tree);
	  }

	  public void addChild(SygMorphoSyntacticTree tree, int i){

	    if(children==null)
	      children=new ArrayList<SygMorphoSyntacticTree>();
	    children.add(i, tree);
	  }

	  public void removeChild(int i){

	    children.remove(i);
	  }

	public static SygMorphoSyntacticTree getRootNode()
	{
		SygMorphoSyntacticInformations root = new SygMorphoSyntacticInformations();
	    root.BAL = "ROOT";
	    return new SygMorphoSyntacticTree(root);
	}
	  
	public SygMorphoSyntacticInformations getInfos() {
		return infos;
	}

	public void setInfos(SygMorphoSyntacticInformations infos) {
		this.infos = infos;
	}

	public ArrayList<SygMorphoSyntacticTree> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<SygMorphoSyntacticTree> children) {
		this.children = children;
	}
	
	public String toString()
	{
	    String res = infos.LEMME;
	    res += '(';
	    if(!isLeaf())
	      for(int i = 0; i < children.size(); i++)
	        res+=children.get(i).toString();
	    res+=')';
	    return res;
	  }

	 public String completeToString()
	 {
	    String res = infos.toString();
	    res += '(';
	    if(!isLeaf())
	      for(int i = 0; i < children.size(); i++)
	        res+=children.get(i).completeToString();
	    res+=')';
	    return res;
	  }
}

