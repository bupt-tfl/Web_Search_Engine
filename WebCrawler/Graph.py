import numpy

class Graph:
    node_id = 0
    def __init__(self):
        self.nodes = {}
        self.q = 0.85 # deal sinks
    def add_node(self, node):
        if node not in self.nodes:
            self.nodes[node] = self.node_id
            self.node_id += 1

    def matrix_for_graph(self):
        matrix = numpy.zeros((self.node_id, self.node_id))
        for node in self.nodes:
            num = len(node.to_page)
            # deal leaks
            if num == 0:
                matrix[self.nodes[node]] = numpy.ones((1,self.node_id))[0] / self.node_id
            for page in node.to_page:
                matrix[self.nodes[node]][self.nodes[page]] = float(1/num)
        return matrix

    def cal_page_rank(self, n = 50):
        base_val = numpy.ones((self.node_id,1)) * float((1-self.q)/self.node_id)
        page_rank = numpy.ones((self.node_id,1))/self.node_id
        matrix = self.matrix_for_graph()
        page_rank = numpy.ones((self.node_id,1)) / self.node_id
        for i in range(n):
            new_page_rank = base_val + matrix.T.dot(page_rank) * self.q
            diff = 0
            for a,b in zip(page_rank,new_page_rank):
                diff += numpy.abs(b-a)
            if diff < 0.00001:
                break
            page_rank = new_page_rank
        return page_rank

    def update_page_rank(self, n = 50):
        page_rank = self.cal_page_rank(n)
        for node in self.nodes:
            node.page_rank = page_rank[self.nodes[node]][0]
