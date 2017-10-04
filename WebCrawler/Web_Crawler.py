import urllib.request as uReq
import urllib.parse as uParse
from bs4 import BeautifulSoup as soup
import queue
import os
import Html_Page as hp
import Graph as g
import time

class Spider:

    def __init__(self):
        self.page_queue = queue.PriorityQueue()
        self.visited_pages = []
        self.graph = g.Graph()

    def crawl_html_page(self, url):
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.15063'}
        u_req = uReq.Request(headers=headers, url=url)
        u_res = uReq.urlopen(u_req)
        page_html = u_res.read()
        u_res.close()
        self.visited_pages.append(url)
        return page_html

    def get_start_page(self, keywords):
        my_url = 'https://www.google.com/search?q={0}'.format(keywords.replace(' ', '+'))
        page_html = self.crawl_html_page(my_url)
        page_soup = soup(page_html, "html.parser")
        containers = page_soup.findAll("h3", {"class": "r"})
        start_pages = []
        for container in containers:
            title_container = container.a
            url = title_container['href']
            if self.is_valid_url(url):
                new_page = hp.HTML_Page(url)
                self.graph.add_node(new_page)
                self.page_queue.put(new_page)
                start_pages.append(url)
        self.graph.update_page_rank()
        return start_pages

    def is_valid_url(self,url):
        if ('cgi' in url) or url.endswith(('.js', '.pdf', 'png', 'jpg', 'jsp', 'asp')):
            return False
        return True

    def bfs_crawler(self, limit=-1, max_page_number=1000):
        temp_queue = queue.PriorityQueue()
        page_inorder = []
        while (self.page_queue.qsize() > 0 or temp_queue.qsize() > 0) and max_page_number > 0:
            # Everytime after we crawl 50 pages, update pagerank
            num = 50
            while self.page_queue.qsize() > 0 and num > 0:
                temp_page = self.page_queue.get()
                if temp_page.page_rank == 0:
                    self.graph.update_page_rank()
                temp_queue.put(temp_page)
                num -= 1
            self.graph.update_page_rank()
            while temp_queue.qsize() > 0:
                page = temp_queue.get()
                print(page.url + ' |' + str(page.page_rank))
                if page.url not in self.visited_pages:
                    try:
                        page_inorder.append(page)
                        max_page_number -= 1
                        page_html = self.crawl_html_page(page.url)
                        page_soup = soup(page_html, "html.parser")
                        page.page_info = time.ctime() + '\n' + page.url + ' size:' + str(len(page_html) / 1024) + 'KB'+' page_rank:' + str(page.page_rank)
                        # deal with inner network
                        for tag in page_soup.findAll('a', href=True)[:limit]:
                            if tag['href'].startswith('http'):
                                new_url = tag['href']
                            else:
                                new_url = uParse.urljoin(page.url, tag['href'])
                            # exclude unvalid urls
                            if self.is_valid_url(new_url):
                                new_page = hp.HTML_Page(new_url)
                                if new_page not in page.to_page:
                                    page.to_page.append(new_page)
                                if new_page not in self.graph.nodes:
                                    self.graph.add_node(new_page)
                                    self.page_queue.put(new_page)
                    except Exception as e:
                        print(page.url + ' error')
                        if os.path.exists('errorLog.log'):
                            with open('errorLog.log', 'a') as log:
                                log.write(time.ctime() + '\n' + page.url + ' |' + str(e) + '\n')
                        else:
                            with open('errorLog.log', 'w') as log:
                                log.write(time.ctime() + '\n' + page.url + ' |' + str(e) + '\n')
        self.graph.update_page_rank()
        return page_inorder

    def write_page_info(self,limit=-1, max_page_number=1000):
        page_inorder = self.bfs_crawler(limit=limit, max_page_number =max_page_number)
        for page in page_inorder:
            if os.path.exists('urlLog.log'):
                with open('urlLog.log', 'a') as url_log:
                    url_log.write(page.page_info + ' | final_rank:' + str(page.page_rank) + '\n')
            else:
                with open('urlLog.log', 'w') as url_log:
                    url_log.write(page.page_info + ' | final_rank:' + str(page.page_rank) + '\n')