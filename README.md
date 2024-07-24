# paper-scout
Paper Scout analyzes your local library of academic papers and generates a graph where:
 - A node corresponds to a paper from your library.
 - An edge indicate that a paper is cited by the other paper.
 - The bigger the node, the more it is cited by other papers.

By clicking on a paper, you'll be able to see what others say about it. This can be useful when trying to summarize your field's current state of the art. You can also use the search field to filter the results.

![interface](https://i.imgur.com/p7STkGS.png)

## Usage
```
--library [path]                   Library to analyze (i.e., a folder containing pdf files of academic papers)
```

To visualize the results, open the HTML file `src/main/html/main.html` once the analysis is complete. Note that, depending on the number of papers in your library, the analysis may take a while.

### Assumptions
Currently assumes that your pdf file names have the form `year - title.pdf`.
