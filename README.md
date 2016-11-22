# ScarceDataNetworkGraph
Building content-driven entity co-occurrence and citation graphs for scarce data

This code was used in the experiments of the research paper

**Reinald Kim Amplayo** and Min Song. **Building Content-driven Entity Networks for Scarce Scientific Literature using Content Information**. _COLING Workshop on Building and Evaluating Resources for Biomedical Text Mining_, 2016.

You will need the following libraries to run this project:
- ABNER
- Jsoup
- JUNG
- Gephi
- Log4j
- Mallet
- SLF4J
- Stanford CoreNLP

To create the graphs, you need to follow these steps:

1. Get the PMC ids of your selected topic from the PMC website
2. Run `PMCCrawler.java` to extract information of the papers
3. Now that you have data, run the extraction tool (named `EntityExtraction.java`, where `Entity` is one of the entities listed below) of the entity you want to build the graph. There are four entities supported:
  a. Author
  b. BioEntity
  c. Keyword
  d. Topic
4. Run the graph construction tool (named `EntityGraphType.java`, where `Entity` is one of the above, and `GraphType` is one of the graph construction method types below). There are three methods supported:
  a. Traditional co-occurrence/collaboration
  b. Content-based co-occurrence/collaboration
  c. Content-based citation
5. For analysis, run the two provided tool for community detection and for page rank in the `calc` package.

To cite the paper/code, please use this BibTex:

```
@inproceedings{amplayo2016,
	Author = {Reinald Kim Amplayo and Min Song},
	Booktitle = {COLING Workshop on Building and Evaluating Resources for Biomedical Text Mining},
	Location = {Osaka, Japan},
	Year = {2016},
	Title = {Building Content-driven Entity Networks for Scarce Scientific Literature using Content Information},
}
```

If you have questions, send me an email: rktamplayo at yonsei dot ac dot kr
