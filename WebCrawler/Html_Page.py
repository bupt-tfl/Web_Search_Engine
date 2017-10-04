class HTML_Page:
    def __init__(self, url, page_rank=float(0)):
        self.url = url
        self.page_rank = page_rank
        self.to_page = []
        self.page_info = ''

    def __lt__(self, other):
        return self.page_rank > other.page_rank

    def __eq__(self, other):
        if not isinstance(other, HTML_Page):
            raise TypeError
        return self.url == other.url
    def __hash__(self):
        return hash(self.url)

    def add_to_page(self, page):
        self.to_page.append(page)
